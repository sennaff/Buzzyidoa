package app;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.ObservableList;

import javafx.event.Event;
import javafx.event.EventHandler;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import jfxtras.labs.util.event.MouseControlUtil;

public class EvidNode extends AnchorPane
{
    public Evidence evidence ;     // Objeto evidência que é representado por este nodo
    
    private MainScreen mainControl ;
    private final EvidNode self;
    
    private boolean locked ;        // armazena o estado atual do nodo quanto à sua editabilidade (true = não editável, false = editável)  
    
    public static final String EVID_STATE_NAME = "evidStateName" ;
    public static final String EVID_STATE_PROB = "evidStateProb" ;
    
    @FXML private AnchorPane evidNode_rootPane;
    
    @FXML private VBox evidNode_contentVBox;
    
    @FXML private HBox evidNode_titleHBox;
    @FXML private Label evidNode_lblNodeType;           // Representa o tipo do nodo, e também é a área clicável que permite a movimentação do nodos
    @FXML private TextField evidNode_nodeNameTxtField ; 
    @FXML private Label evidNode_closeBtn;              // Botao para apagar o nodo
    
    @FXML private GridPane evidNode_colTitlesGrid ;     // Tabela que mostra os nomes dos estados da hipótese referentes a cada coluna de probs da evidencia
    @FXML private GridPane evidNode_statesGrid;         // tabela de estados da hipótese
    
//    //Elementos de uma "linha" do nodo de hipotese
//    @FXML private TextField hypNode_stateNameTxtField ;  //nome do estado da hiptoese
//    @FXML private TextField hypNode_stateProbTxtField ;  //probabilidade do estado da hipotese
////    @FXML private BorderPane hypNode_killStateBordPane;  //conteiner do botao de remoção de estado da hipotese
//    @FXML private Button hypNode_killStateBtn ;     //botao de remoção de estado da hipotese
           
    @FXML private BorderPane evidNode_addStateBtnBorder;
    @FXML private Button evidNode_addStateBtn ;
    @FXML private Button btn_lockEvidNode ;
    
//    private static final int numHypColumns = 3;
//
//    private EventHandler <MouseEvent> mLinkHandleDragDetected;
//    private EventHandler <DragEvent> mLinkHandleDragDropped;
//    private EventHandler <DragEvent> mContextLinkDragOver;
//    private EventHandler <DragEvent> mContextLinkDragDropped;
//
//    private EventHandler <DragEvent> mContextDragOver;
//    private EventHandler <DragEvent> mContextDragDropped;   
 
    
    //Cria o controlador do nodo de hipótese e o associa ao .fxml correspondente
    public EvidNode() 
    {
        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("/userInterface/EvidNode.fxml") );
        fxmlLoader.setRoot(this); 
        fxmlLoader.setController(this);
        self = this;
        try
        { 
            fxmlLoader.load();
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
        //provide a universally unique identifier for this object
        setId(UUID.randomUUID().toString());
    }

    @FXML
    private void initialize()
    {       
    }
    
     public boolean isLocked()
    {
        return this.locked;
    }

    public void setLocked(boolean lockState)
    {
        this.locked = lockState;
    }        
    
    
    public void setMainControl (MainScreen control)
    {
        this.mainControl = control ;
    }
    
    public String getEvidNodeName()
    {
        return this.evidNode_nodeNameTxtField.getText();
    }
    
    @FXML // apaga um nodo de hipotese
    private void removeEvidNode() 
    {
        // Aponta para o contêiner imediato do nodo de evidência e remove a si mesmo dele
        AnchorPane parent  = (AnchorPane) self.getParent();        
        parent.getChildren().remove(self);
        
        // Verifica se a evidência apagada era a última existente
        if ( !self.mainControl.hasEvidences() )
        {
            // Habilita novamente a edição do nodo de hipótese
            self.mainControl.getHypNode().lockHypNode();
            
            // Verifica se a opção de compilação da rede não está habilitada
            if ( !self.mainControl.btnCompileNetwork.isDisable() )
            {
                // Se o botão de compilação da rede estiver habilitado, então deve-se desativá-lo
                self.mainControl.btnCompileNetwork.setDisable(true);
            }
        }
        else
        {
            // Verifica se as demais evidências estão corretas
            boolean evidsCorrect = self.mainControl.isWithAllEvidsComplete();
            if ( evidsCorrect )
            {
                // Se as demais evidências estiverem corretas, habilita a opção de compilação da rede
                self.mainControl.btnCompileNetwork.setDisable(false);
            }            
        }
    }

    @FXML // adiciona um novo estado ao nodo de hipotese
    private void addEvidState() 
    {
        int numRows = this.getRowCount(evidNode_statesGrid); // conta o numero de linhas no grid (tabela de estados da evid)
        int numCols = this.getColCount(evidNode_statesGrid); // conta o numero de colunas no grid (tabela de estados da hipotese)
        
        Node[] rowComponents = this.evidRowSetup(numRows); // monta uma linha "padronizada" para adicionar ao nodo de hipótese
        int lastRowIndex = rowComponents.length-1 ;       // aponta para o índice do botão na tabela, para ajustá-lo na inserção no grid
        
        this.evidNode_statesGrid.addRow(numRows, rowComponents );    // adiciona a linha "padronizada" ao grid do nodo
        this.evidNode_statesGrid.setColumnIndex(rowComponents[0], 0);// atribui explicitamente a posição "0" referente aos itens da coluna 0
//        GridPane.setColumnIndex(rowComponents[0], 0) ;
        this.evidNode_statesGrid.setHalignment(rowComponents[lastRowIndex], HPos.RIGHT); // define o ajuste de alinhamento adequado para o botão dentro de sua célula
     
        
//        for (int colIndex = 0; colIndex < numCols; colIndex++)
//        {
//            ColumnConstraints cc = new ColumnConstraints();
//            cc.setHgrow(Priority.ALWAYS) ;             // habilita o crescimento horizontal da coluna 
//            cc.setFillWidth(true);          // faz com que os nodos presentes nas células preencham-nas totalmente
//            this.evidNode_statesGrid.getColumnConstraints().add(cc);    // envia ao grid as novas definições
//        }        
        
//        System.out.println ("\nGridpane dimensions: (" + this.getRowCount(evidNode_statesGrid) + 
//                            "x" + this.getColCount(evidNode_statesGrid) + ")" +
//                            "\nLinhas: " + this.getRowCount(evidNode_statesGrid) +
//                            "\nColunas: " + this.getColCount(evidNode_statesGrid));
    }
    
    //conta as linhas de um gridpane
    private int getRowCount(GridPane pane)
    {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) 
        {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) 
            {
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null)
                {
                    numRows = Math.max(numRows,rowIndex+1);
                }
            }
        }
        return numRows;
    }

    //conta as colunas de um gridpane
    private int getColCount(GridPane pane)
    {
        int numCols = pane.getColumnConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++)
        {
            Node child = pane.getChildren().get(i);
            if (child.isManaged())
            {
                Integer colIndex = GridPane.getColumnIndex(child);
                if(colIndex != null)
                {
                    numCols = Math.max(numCols,colIndex+1);
                }
            }
        }
        return numCols;
    }
    
    //supostamente deveria pegar um nodo do grid a partir do índice, mas não parece funcionar
    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane)
    {
        Node result = null;
        ObservableList<Node> children = gridPane.getChildren();

        for (Node node : children)
        {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column)
            {
                result = node;
                break;
            }
        }

    return result;
}
    
    // Prepara os títulos das colunas da tabela de probabilidades
    public void colTitlesSetup()
    {
//        int numRows = this.getRowCount(evidNode_statesGrid); // conta o numero de linhas no grid (tabela de estados da evid)
        int numberOfHypStates = this.mainControl.getHypNode().getCountHypStates();   // guarda a quantidade de estados da hipótese
        Node[] rowComponents = new Node[2+numberOfHypStates]; //2 (nome do estado e botão de exlusão) + quantidade de estados da hipótese
        
        // Label da coluna[0], deve estar vazia pois é a coluna dos nomes dos estados da evidencia
        Label blankLeft = new Label("");
        blankLeft.setMinWidth(90);
        rowComponents[0] = blankLeft ;
        
        // Referencia a tabela de estados da hipótese
        GridPane hypStatesGrid = this.mainControl.getHypNode().hypNode_statesGrid ;
        
        for (int i = 1 ; i<= numberOfHypStates ; i++)
        {
            // Encontra o TextField com o nome do estado desejado
            TextField hypStateNameTxtField = (TextField) this.mainControl.getHypNode().getNodeByRowColumnIndex(i-1, 0, hypStatesGrid);
            
            Label colTitle = new Label(hypStateNameTxtField.getText());            
            colTitle.getStyleClass().clear();
            colTitle.setMinHeight(0);
            colTitle.setMinWidth(90);
            colTitle.setMaxWidth(90);
            colTitle.getStyleClass().add("colTitle-lable");
            rowComponents[i] = colTitle ;
        }    
        
        int lastRowIndex = rowComponents.length-1 ;       // aponta para o índice do botão na tabela, para ajustá-lo na inserção no grid
        
        AnchorPane blankRight = new AnchorPane();
        blankRight.setMinWidth(25);
        rowComponents[lastRowIndex] = blankRight ;
        
        this.evidNode_colTitlesGrid.getStyleClass().clear();
        this.evidNode_colTitlesGrid.getStyleClass().add("colTitle-bg");
        this.evidNode_colTitlesGrid.addRow(0, rowComponents );    // adiciona a linha "padronizada" ao topo do grid do nodo
        this.evidNode_colTitlesGrid.setColumnIndex(rowComponents[0], 0);// atribui explicitamente a posição "0" referente aos itens da coluna 0
//        GridPane.setColumnIndex(rowComponents[0], 0) ;
//        this.evidNode_statesGrid.setHalignment(rowComponents[lastRowIndex], HPos.RIGHT); // define o ajuste de alinhamento adequado para o botão dentro de sua célula
    }
    
    //Prepara uma nova linha para ser adicionada no nodo de evidência
    //Linhas representam os estados da Evidência, e contêm: Nome do estado, P(e/H1), P(e/H2)... P(e/Hn), botão para remover estado
    //Quantidade variável de colunas, dependendo da quantidade de estados do nodo de hipótese
    private Node[] evidRowSetup(int rowCount)
    {
        int currentState = rowCount+1; //recebe o numero atual de linhas, e adiciona 1 para a próxima a ser criada
        int numberOfHypStates = this.mainControl.getHypNode().getCountHypStates();   // guarda a quantidade de estados da hipótese
        Node[] rowComponents = new Node[2+numberOfHypStates]; //2 (nome do estado e botão de exlusão) + quantidade de estados da hipótese
        
        //cria o textField do nome do novo estado
        TextField evidStateNameTxtField = new TextField();  //
        evidStateNameTxtField.setPromptText("Estado" + currentState);        
        evidStateNameTxtField.getStyleClass().add("textField-node");
        
        // concede ao textField do nome do novo estado um identificador de tipo único
        evidStateNameTxtField.setId(this.EVID_STATE_NAME);
        
        evidStateNameTxtField.prefColumnCountProperty().bind(evidStateNameTxtField.textProperty().length());
//        evidStateNameTxtField.setPadding(Insets.EMPTY);
        evidStateNameTxtField.setMinWidth(90);
//        evidStateNameTxtField.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
//        evidStateNameTxtField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //adiciona o componente criado na ordem em que deverá aparecer ta tabela do nodo de evidência        
        rowComponents[0] = evidStateNameTxtField ;        
        
        for (int i = 1 ; i<= numberOfHypStates ; i++)
        {
            //cria o textField da prob do novo estado
            TextField evidStateProbTxtField = new TextField();  //
            evidStateProbTxtField.setPromptText("P(e" + currentState + "|H" + i + ")");        
            evidStateProbTxtField.getStyleClass().add("textField-node");
            evidStateProbTxtField.setTextFormatter(this.formatNumericTextField()); //adiciona o filtro de formato numérico ao textField de probabilidades
            
            // concede ao textField da prob do novo estado um identificador de tipo único
            evidStateProbTxtField.setId(this.EVID_STATE_PROB);
            
//            evidStateProbTxtField.setPadding(Insets.EMPTY);
            evidStateProbTxtField.setMinWidth(90);

//            evidStateNameTxtField.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
            rowComponents[i] = evidStateProbTxtField ;
        }        
        
        //cria o botão de remoção do novo estado
        Button killEvidStateBtn = new Button();  //
        killEvidStateBtn.setText("-");        
        killEvidStateBtn.getStyleClass().add("btn-kill-hyp-state");
        killEvidStateBtn.setPickOnBounds(false); // permite clickar apenas na área visível do botão
        
        // concede ao botão um identificador de tipo único
        killEvidStateBtn.setId("killHypStateBtn" + currentState);
        
        //Concede ao botão a capacidade de lidar com "clicks" recebidos
        killEvidStateBtn.addEventHandler
        (   
            MouseEvent.MOUSE_CLICKED,
            new EventHandler<MouseEvent>() 
            {
                @Override public void handle(MouseEvent e)
                {
                    removeEvidState(e); //método executado quando o botão é clickado
                }
            }
        );
        
        int lastPos = rowComponents.length-1 ;       // aponta para a última coluna
        rowComponents[lastPos] = killEvidStateBtn ;  // adiciona o último componente da linha (botão de exclusão de estado)
                  
        return rowComponents;
    }
    
    @FXML   // Destaca o botão de exclusão do nodo de hipótese quando o mouse passa sobre ele
    private void closeButtonHighlightOn()
    {
        this.evidNode_closeBtn.getStyleClass().clear();
        this.evidNode_closeBtn.getStyleClass().add("hyp-title-fonts");
        this.evidNode_closeBtn.getStyleClass().add("close-button-highlight-on");
    }
    
    @FXML   // Remove o destaque do botão de exclusão do nodo de hipótese quando o mouse passa sobre ele
    private void closeButtonHighlightOff()
    {
        this.evidNode_closeBtn.getStyleClass().clear();
        this.evidNode_closeBtn.getStyleClass().add("hyp-title-fonts");
        this.evidNode_closeBtn.getStyleClass().add("close-button-highlight-off");
    }

    @FXML
    private void removeEvidState(Event event) // deve apagar uma linha inteira do nodo de hipótese após o botão referente a ela ser clicado
    {       
        // Acessa o botão que originou o evento de exclusão do estado da hipótese
        Button btn = (Button) event.getSource();
        
        // Acessa o grid ao qual o botão clickado pertence (tabela de estados)
        GridPane grid = (GridPane) btn.getParent();
        
        // Informa a linha do grid à qual o botão clickado pertence
        int numRow = this.evidNode_statesGrid.getRowIndex(btn);
        
        // Deleta do grid a linha referente ao botão clickado
        this.deleteRow(this.evidNode_statesGrid, numRow);
        
//        System.out.println( "Id do botão clickado: " + btn.getId());
//        System.out.println( "Id do GridPane clickado: " + grid.getId());
//        System.out.println( "Linha do botao clickado: " + numRow);
//        System.out.println( "Linha apagada: " + numRow);
//        int numChildren = this.evidNode_statesGrid.getChildren().size();// Retorna a quantidade total de nodos filhos do grid
//        System.out.println( "Quantidade de filhos do grid: " + numChildren);
        
        //this.updatePromptTexts(this.hypNode_statesGrid) // atualiza os textos "default" dos campos de texto após uma linha ser apagada
    }
    
    // Apaga uma determinada linha de um gridPane
    static void deleteRow(GridPane grid, final int row) //  Apaga uma linha específica de um determinado gridPane
    {
        Set<Node> deleteNodes = new HashSet<>();    // reune os nodos que serão excluídos
        for (Node child : grid.getChildren())       // percorre todos os nodos do grid
        {            
            Integer rowIndex = GridPane.getRowIndex(child);     // get row index from current child
            
            int r = rowIndex == null ? 0 : rowIndex; // handle null values for index = 0

            if (r > row)
            {                
                GridPane.setRowIndex(child, r-1);   // decrement rows for rows after the deleted row
            } 
            else if (r == row)
            {                
                deleteNodes.add(child);             // collect matching rows for deletion
            }
        }        
        grid.getChildren().removeAll(deleteNodes);  // remove nodes from row
        
//        // Agora percorre novamente o grid, para atualizar os promptTexts dos textFields (erro ConcurrentModificationException)
//        for (Node child : grid.getChildren())       // percorre todos os nodos do grid após a exclusão
//        {            
//            Integer rowIndex = GridPane.getRowIndex(child);     // get row index from current child
//            Integer colIndex = GridPane.getColumnIndex(child) ; // get column index from current child
//            
//            int r = rowIndex == null ? 0 : rowIndex;    // handle null values for index = 0
//            int col = colIndex == null ? 0 : colIndex;  // handle null values for index = 0
//
//            if ( col == 0 && (child instanceof TextField)) // verifica se "child" é um texField referente a nome de estado
//            {
//                int textRow = r+1 ;
//                TextField child2 = (TextField) child ;      // cria uma copia do textField atual
//                child2.setPromptText("Estado" + textRow);   // atualiza o promptText dele
//                grid.getChildren().remove(child);           // remove o textField desatualizado
//                grid.add(child2, col, r) ;                  // insere o novo textField alterado no lugar do atual
//            }
//        }
    }
    
    static void updatePromptTexts(GridPane grid) // INUTIL POR AGORA
    {
////        Set<Node> deleteNodes = new HashSet<>();
//        for (Node child : grid.getChildren())
//        {
//            // get index from child
//            Integer rowIndex = GridPane.getRowIndex(child);
//
//            // handle null values for index=0
//            int r = rowIndex == null ? 0 : rowIndex;
//
////            if (r > row)
////            {
////                // decrement rows for rows after the deleted row
////                GridPane.setRowIndex(child, r-1);
////            } 
////            else if (r == row)
////            {
////                // collect matching rows for deletion
////                deleteNodes.add(child);
////            }
//        }
////        // remove nodes from row
////        grid.getChildren().removeAll(deleteNodes);
    }
    
    @FXML
    private void lockEvidNode()
    {
        // Verifica se o nodo está "locked" (sem permitir edição)
        if ( this.isLocked() )
        {
            // É feito o "unlock" do nodo, para permitir edição de seus conteúdos
            this.setLocked(false);
            
            self.mainControl.btnCreateEvidNode.setDisable(true); // desabilita a criação de evidências
            this.evidNode_nodeNameTxtField.setDisable(false);
            this.evidNode_statesGrid.setDisable(false);
            this.evidNode_addStateBtn.setDisable(false);
            
            // Verifica se a opção de compilação da rede não está habilitada
            if ( !this.mainControl.btnCompileNetwork.isDisable() )
            {
                // Se o botão de compilação da rede estiver habilitado, então deve-se desativá-lo
                this.mainControl.btnCompileNetwork.setDisable(true);
            }
        }
        else // Se o nodo de evidência está editável
        {
            // É feito o "lock" do nodo, para impedir a edição de seus conteúdos
            this.setLocked(true);
            
            this.evidNode_nodeNameTxtField.setDisable(true);
            this.evidNode_statesGrid.setDisable(true);
            this.evidNode_addStateBtn.setDisable(true);
            
            if ( this.isEvidNodeComplete() )
            {
                self.mainControl.btnCreateEvidNode.setDisable(false); //habilita a criação de evidências
                
                // Verifica se as demais evidências estão corretas
                boolean evidsCorrect = self.mainControl.isWithAllEvidsComplete();
                if ( evidsCorrect )
                {
                    // Se as demais evidências estiverem corretas, habilita a opção de compilação da rede
                    self.mainControl.btnCompileNetwork.setDisable(false);
                }
            }
        }
    }
    
    public void lockSpecificNode()
    {
        // Verifica se o nodo não está "locked" (ou seja, se ele está permitindo edição)
        if ( !this.isLocked() )
        {
            // É feito o "lock" do nodo, para impedir a edição de seus conteúdos
            this.setLocked(true);

            this.evidNode_nodeNameTxtField.setDisable(true);
            this.evidNode_statesGrid.setDisable(true);
            this.evidNode_addStateBtn.setDisable(true);            
        }        

//        if ( this.isEvidNodeComplete() )
//        {
////            self.mainControl.btnCreateEvidNode.setDisable(false); //habilita a criação de evidências
//
//            // Verifica se as demais evidências estão corretas
////            boolean evidsCorrect = self.mainControl.isWithAllEvidsComplete();
////            if ( evidsCorrect )
////            {
////                // Se as demais evidências estiverem corretas, habilita a opção de compilação da rede
////                self.mainControl.btnCompileNetwork.setDisable(false);
////            }
//        }
    }
        
    // Método que informa a quantidade de linhas (estados) presente no nodo de evidência
    public int getCountEvidStates() 
    {
        return this.getRowCount(this.evidNode_statesGrid) ; // conta as linhas do grid - cada linha é 1 estado
    }
    
    // Verifica se todos os campos do nodo de evidência foram preenchidos
    // Apenas permitirá a criação de nodos de evidência caso o retorno seja "true"
    public boolean isEvidNodeComplete()
    {
        if ( this.evidNode_nodeNameTxtField.getText().isEmpty() ) // verifica se o nome do nodo foi adicionado
        {
            String erro = "ERRO: Evidência sem nome! Todo nodo de evidência deve possuir um nome!" ;
            this.mainControl.notifyError(erro);
            System.out.println (erro);
            return false;
        }
        else
        {
            this.mainControl.clearStyleErrorTab();
        }
        
        if ( this.getRowCount(this.evidNode_statesGrid) < 1 ) // verifica se pelo menos 1 estado foi adicionado
        {
            String evidName = this.getEvidNodeName() ;
            String erro = "ERRO: Nenhum estado adicionado à evidência \"" + evidName +
                          "\". O nodo de evidência deve possuir ao menos 1 estado!";
            System.out.println (erro);
            this.mainControl.notifyError(erro);
            return false;
        }
        else
        {
            this.mainControl.clearStyleErrorTab();
        }
        
        for (Node child : self.mainControl.getAllNodes(this)) // percorre todos os nodos do grid
        {
//            System.out.println ( "\n >>> ID do nodo atual: " + child.getId() + " / StyleType:" + child.getStyleClass());
            if ( child instanceof TextField ) // verifica se "child" é um texField referente a nome de estado
            {
                TextField child2 = (TextField) child ;      // cria uma copia do textField atual
//                System.out.println ( "Texto lido no textField: " + child2.getText() );
                if ( child2.getText().isEmpty() )
                {
                    String evidName = this.getEvidNodeName() ;
                    String erro = "ERRO: 1 ou mais campos de texto não preenchido(s) na evidência \"" 
                                    + evidName + "\"!";
                    System.out.println (erro);
                    this.mainControl.notifyError(erro);                    
                    return false ;
                }
                else
                {
                    this.mainControl.clearStyleErrorTab();
                }
            }
        }
        
        int numOfStates = this.getRowCount(this.evidNode_statesGrid);    // verifica o número de linhas/estados do gridpane
        int numOfCols = this.getColCount(this.evidNode_statesGrid) ;     // verifica o número de colunas do gridpane
        
//        System.out.println ( "\n\n >>>> Dimensões do grid = Linhas: " + numOfStates + 
//                             "\n >>>>>>>>>>>>>>>>>>>>>>>> Colunas: " + numOfCols + "\n");
        
        // Percorre cada coluna do gridpane, exceto a última (que possui apenas botões)
        for(int col = 1; col <= numOfCols-2 ; col++)
        {
            float colSum = 0f ; // guarda a soma das probs de uma coluna

            // Percorre as linhas da coluna atual atrás de cada valor de probabilidade o agrega ao colSum
            for (int row = 0; row <= numOfStates-1 ; row++) 
            {
                // Armazena o campo de texto atual para extrair o valor de seu texto
                TextField childText = (TextField) this.getNodeByRowColumnIndex(row, col, this.evidNode_statesGrid) ;
                String input = childText.getText();
                
                float prob = Float.parseFloat(input) ;   // Conversão do valor textual para numérico
                colSum += prob ;                            // Agrega o valor lido à soma atual da coluna corrente
                
//                Node test = this.getNodeByRowColumnIndex(j, i, this.evidNode_statesGrid) ;
//                System.out.println ( "\n >>> Valor do nodo atual: " + input);
//                System.out.println ( "\n >>>> Tipo do nodo na posição (" + j + "," + i + "): " + test.getId());
            }            
//            System.out.println ("\n>>> Soma da coluna \"" + col + "\" = " + colSum);
            
            // Se a soma das probabilidades em uma coluna não for igual a "1", é apontado um erro
            if ( colSum != 1)
            {
                String evidName = this.getEvidNodeName() ;
                String erro = "ERRO - Evidência \"" 
                              + evidName + "\": A Soma das probabilidades de cada coluna deve ser 1!";
                System.out.println (erro);
                this.mainControl.notifyError(erro);                    
                return false ;
            }
            else // Nesse caso, o nodo de evidência foi preenchido de forma correta
            {
                this.mainControl.clearStyleErrorTab(); // Apagam-se as mensagens de erro                
            }
        }        
        return true ;
    }
    
    // Cria um formatador de texto para garantir apenas inputs numéricos nos textFields de probabilidades
    public TextFormatter formatNumericTextField ()
    {
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*"); // filtro para permitir apenas inputs numéricos separados por até 1 único "."       
        TextFormatter formatter = new TextFormatter
        (
            ( UnaryOperator<TextFormatter.Change> ) change -> 
            {
                return pattern.matcher(change.getControlNewText()).matches() ? change : null;
            }
        );
        
        return formatter ;
    }
    
    // Desabilita a exclusão do nodo de evidência
    public void lockEvidExclusion()
    {
        this.evidNode_closeBtn.setDisable(true);
    }
    
    // Reabilita a exclusão do nodo de evidência
    public void unlockEvidExclusion()
    {
        this.evidNode_closeBtn.setDisable(false);
    }
    
    // Desabilita a edição do nodo de evidência
    public void lockEvidEdition()
    {
        this.btn_lockEvidNode.setDisable(true);
    }
    
    // Reabilita a edição do nodo de evidência
    public void unlockEvidEdition()
    {
        this.btn_lockEvidNode.setDisable(false);
    }
        
     // Assimila os inputs fornecidos nos campos do nodo de evidência
    public void computeEvidInputs()
    {
        // Cria o novo objeto de evidência
        this.evidence = new Evidence();
        
        // Associa o nome do nodo de evidência
        this.evidence.setNodeName(this.evidNode_nodeNameTxtField.getText());
        
        int numOfStates = this.getRowCount(this.evidNode_statesGrid);    // Verifica o número de linhas/estados do gridpane
        int numOfCols = this.getColCount(this.evidNode_statesGrid) ;     // Verifica o número de colunas do gridpane
        
        // Percorre cada linha do gridpane para criar os estados (cada linha é um estado)
        for(int row = 0; row <= numOfStates-1 ; row++)
        {
            EvidenceState evidState = new EvidenceState() ; // Objeto que representa um estado da hipótese
            
            // Array temporario para armazenar os conjuntos de probabilidades de cada estado da evidencia
            // Probabilidades referentes a: P(e|Hi) = Probs_Evid_given_Hyp 
            ArrayList<Float> probsPerState = new ArrayList<Float>();    
            
            // Percorre cada coluna do gridpane (exceto a última, que só tem botões)
            for(int col = 0; col <= numOfCols-2 ; col++)
            {
                // Armazena o campo de texto atual para extrair o valor de seu texto
                TextField childText = (TextField) this.getNodeByRowColumnIndex(row, col, this.evidNode_statesGrid) ;
                String input = childText.getText();
                
                // Verifica se é o nome do estado
                if ( childText.getId().equals(this.EVID_STATE_NAME) )
                {
                    evidState.setNameEvidState(input);
                }
                // Se não for um nome de estado, espera-se que seja uma probabilidade
                else if ( childText.getId().equals(this.EVID_STATE_PROB) )
                {
                    float prob = Float.parseFloat(input) ;  // Converte o texto de entrada para um valor numérico
                    probsPerState.add(prob);                // armazena a nova probabilidade lida:  P(e|Hi)
                }                
            }
            // Neste ponto, terminou-se a leitura das probs "P(e|Hi)" fornecidas para este estado           
            // Vai armazenar o valor da probabilidade do estado da evidencia "p(e)" apos calculado
            float probEvidState = 0f;    // P(e)
            
            // Representa a hipótese da rede atual
            Hypothesis hyp = this.mainControl.getHypNode().getHypothesis();
            
            // Calcula a probabilidade do estado da evidencia "P(e)" -> vide classe "EvidenceState"
            for (int m = 0 ; m < probsPerState.size() ; m++)
            {
                probEvidState += probsPerState.get(m) * hyp.getHypStates().get(m).getProb_Hyp() ;
            }
            
//            //Armazena o estado que deve receber as probabilidades no momento
//            EvidenceState currentEvidState = actualEvid.getEvidenceStates().get(actualEvidState);

            //Associa o P(e) calculado ao estado atual
            evidState.setProbEvidState(probEvidState);

            //Associa o array de probabilidades ao estado atual
            evidState.setProbs_Evid_given_Hyp(probsPerState);

            //Coloca o estado atual na evidencia atual
            this.evidence.getEvidenceStates().add(evidState);
            
//            // Adiciona o estado recém-lido na hipótese
//            this.hypothesis.addHypState(hypState);
//            
//            System.out.println("\n\nEstado adicionado à " + this.hypothesis.showStates());
            
//            
//            System.out.println("\n\nEstado adicionado à Hipótese \"" + this.hypothesis.getNodeName() + "\": " +
//                                this.hypothesis.getHypStates().get(row).getNameHypState());
//            System.out.println("\n\nQuantidade de estados na hipótese criada: " +
//                                this.hypothesis.getHypStates().size());
        }
        // Neste ponto, a evidência foi devidamente criada, então deve-se adicioná-la à hipótese da rede
        this.mainControl.getHypNode().hypothesis.addEvidence(evidence);
        
    }
    
    
}
