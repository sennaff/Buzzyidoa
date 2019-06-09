package app;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
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
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import jfxtras.labs.util.event.MouseControlUtil;

public class HypNode extends AnchorPane
{
    public Hypothesis hypothesis ;             // Objeto hipótese que é representado por este nodo
    
    private MainScreen mainControl ;            // Referencia o controlador principal   
    private final HypNode self;                 // Referência do objeto corrente a ele mesmo
    
    private static final int numHypColumns = 3; // A quantidade de colunas do nodo de hipótese sempre será 3: (Nome do estado / Prob do estado / Botão de remoção)
    private boolean locked ;                    // Armazena o estado atual do nodo quanto à sua editabilidade (true = não editável, false = editável)  
    
    public static final String HYP_STATE_NAME = "hypStateName" ;
    public static final String HYP_STATE_PROB = "hypStateProb" ;
    
    
    @FXML public AnchorPane hypNode_rootPane;
    
    @FXML public HBox hbox_hypNode ;
    @FXML public Button btn_lockHypNode ;
    @FXML public Tooltip tooltip_lockHypNode ;
    
    @FXML public VBox hypNode_contentVBox;
    
    @FXML public HBox hypNode_titleHBox;
    @FXML public Label hypNode_lblNodeType;         // Representa o tipo do nodo, e também é a área clicável que permite a movimentação do nodos
    @FXML public TextField hypNode_nodeNameTxtField ; 
    @FXML public Label hypNode_closeBtn;            // Botao para apagar o nodo
    
    @FXML public GridPane hypNode_statesGrid;      // tabela de estados da hipótese
    
    //Elementos de uma "linha" do nodo de hipotese
//    @FXML public TextField hypNode_stateNameTxtField ;  //nome do estado da hiptoese
//    @FXML public TextField hypNode_stateProbTxtField ;  //probabilidade do estado da hipotese
////    @FXML private BorderPane hypNode_killStateBordPane;  //conteiner do botao de remoção de estado da hipotese
//    @FXML public Button hypNode_killStateBtn ;     //botao de remoção de estado da hipotese
           
    @FXML public BorderPane hypNode_addStateBtnBorder;
    @FXML public Button hypNode_addStateBtn ;
        
//    public int quantHypStates ; // guarda a quantidade de estados já adicionados à hipótese
//
//    private EventHandler <MouseEvent> mLinkHandleDragDetected;
//    private EventHandler <DragEvent> mLinkHandleDragDropped;
//    private EventHandler <DragEvent> mContextLinkDragOver;
//    private EventHandler <DragEvent> mContextLinkDragDropped;
//
//    private EventHandler <DragEvent> mContextDragOver;
//    private EventHandler <DragEvent> mContextDragDropped;    
    
    //Cria o controlador do nodo de hipótese e o associa ao .fxml correspondente
    public HypNode() 
    {
        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("/userInterface/HypNode.fxml") );
        fxmlLoader.setRoot(this); 
        fxmlLoader.setController(this);
        self = this;
        self.setId("HypNodeUI");
        this.setLocked(false);      // inicializa o nodo em modo editável
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
        this.mainControl.setId("MainScreenUI");
    }
    
    public String getHypNodeName()
    {
        return this.hypNode_nodeNameTxtField.getText();
    }

    public Hypothesis getHypothesis()
    {
        return hypothesis;
    }

    public void setHypothesis(Hypothesis hypothesis) {
        this.hypothesis = hypothesis;
    }
    
    
    
    @FXML // apaga um nodo de hipotese
    private void removeHypNode() 
    {
        // Como o programa remove também todas as evidências dependentes desta hipótese,
        // é verificado se há evidências. Se houver, requisita uma confirmação adicional
        if ( this.mainControl.hasEvidences() )
        {
            boolean userConfirm = this.warningHypRemoval() ; // Requisita confirmação do usuário
            
            if ( userConfirm ) // Se o usuário confirmar, procede com a exclusão 
            {
                // Verifica se a opção de compilação da rede não está habilitada
                if ( !this.mainControl.btnCompileNetwork.isDisable() )
                {
                    // Se o botão de compilação da rede estiver habilitado, então deve-se desativá-lo
                    this.mainControl.btnCompileNetwork.setDisable(true);
                }                
                
                // Remove todas as evidências
                this.mainControl.removeAllEvidences();
                
                // Habilita novamente a criação de um nodo de hipótese
                this.mainControl.btnCreateHypNode.setDisable(false);

                // Desabilita a criação de nodos de evidência
                this.mainControl.btnCreateEvidNode.setDisable(true);
                
                // Desabilita eventuais mensagens de erro geradas pela rede a ser excluída
                this.mainControl.clearStyleErrorTab();

                // Aponta para o "pai" imediato do nodo de hipótese
                AnchorPane parent  = (AnchorPane) self.getParent();
                
                // Enfim realiza a exclusão do nodo de hipótese
                parent.getChildren().remove(self);
            }
//            else
//            {
//                return ;
//            }
            
        }
        else // Se não há evidências, a exclusão do nodo de hipótese é feita normalmente
        {
            // Habilita novamente a criação de um nodo de hipótese
            this.mainControl.btnCreateHypNode.setDisable(false);

            // Desabilita a criação de nodos de evidência
            this.mainControl.btnCreateEvidNode.setDisable(true);
            
            // Desabilita eventuais mensagens de erro geradas pela rede a ser excluída
            this.mainControl.clearStyleErrorTab();

            // Aponta para o "pai" imediato do nodo de hipótese
            AnchorPane parent  = (AnchorPane) self.getParent();

            // Enfim realiza a exclusão do nodo de hipótese
            parent.getChildren().remove(self);
        }
       
        
//        this.mainControl.removeAllEvidences();
        

        // Método de remoção alternativo que parte da "raiz" da aplicação        
//        this.mainControl.removehypNode(this); // Funciona, mas não está em uso no momento
    }

    @FXML // adiciona um novo estado ao nodo de hipotese
    private void addHypState() 
    {
        int numRows = this.getRowCount(hypNode_statesGrid); // conta o numero de linhas no grid (tabela de estados da hipotese)
        int numCols = this.getColCount(hypNode_statesGrid); // conta o numero de colunas no grid (tabela de estados da hipotese)
        
        Node[] rowComponents = this.hypRowSetup(numRows); // monta uma linha "padronizada" para adicionar ao nodo de hipótese
        int lastRowIndex = rowComponents.length-1 ;       // aponta para o índice do botão na tabela, para ajustá-lo na inserção no grid
        
        this.hypNode_statesGrid.addRow(numRows, rowComponents );    // adiciona a linha "padronizada" ao grid do nodo
        this.hypNode_statesGrid.setColumnIndex(rowComponents[0], 0);// atribui explicitamente a posição "0" referente aos itens da coluna 0
//        this.hypNode_statesGrid.setHalignment(rowComponents[0], HPos.LEFT); // define o ajuste de alinhamento adequado para o botão dentro de sua célula
        this.hypNode_statesGrid.setHalignment(rowComponents[lastRowIndex], HPos.RIGHT); // define o ajuste de alinhamento adequado para o botão dentro de sua célula
        
//        System.out.println ("\nGridpane dimensions: (" + this.getRowCount(hypNode_statesGrid) + 
//                            "x" + this.getColCount(hypNode_statesGrid) + ")" +
//                            "\nLinhas: " + this.getRowCount(hypNode_statesGrid) +
//                            "\nColunas: " + this.getColCount(hypNode_statesGrid));
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
    
    //Prepara uma nova linha para ser adicionada no nodo de hipotese
    //Linhas representam os estados da hipótese, e contêm: Nome do estado, P(Hi), botão para remover estado
    private Node[] hypRowSetup(int rowCount)
    {
        int currentState = rowCount+1; //recebe o numero atual de linhas, e adiciona 1 para a próxima a ser criada
        Node[] rowComponents = new Node[this.numHypColumns]; //é 3 por padrão, devido ao número de colunas do nodo de hipótese
        
        //cria o textField do novo estado
        TextField hypStateNameTxtField = new TextField();
        hypStateNameTxtField.setPromptText("Estado" + currentState);        
        hypStateNameTxtField.getStyleClass().add("textField-node");
        hypStateNameTxtField.prefColumnCountProperty().bind(hypStateNameTxtField.textProperty().length());
        
        // concede ao textField do nome do novo estado um identificador de tipo único
        hypStateNameTxtField.setId(this.HYP_STATE_NAME);
        
        //cria o textField da prob do novo estado
        TextField hypStateProbTxtField = new TextField();
        hypStateProbTxtField.setPromptText("P(H" + currentState + ")");        
        hypStateProbTxtField.getStyleClass().add("textField-node");
        
        //adiciona o filtro de formato numérico ao textField de probabilidades
        hypStateProbTxtField.setTextFormatter(this.formatNumericTextField());
        
        // concede ao textField da prob do novo estado um identificador de tipo único
        hypStateProbTxtField.setId(this.HYP_STATE_PROB);
        
        //cria o botão de remoção do novo estado
        Button killHypStateBtn = new Button();
        killHypStateBtn.setText("-");        
        killHypStateBtn.getStyleClass().add("btn-kill-hyp-state");
        killHypStateBtn.setPickOnBounds(false); // permite clickar apenas na área visível do botão
        
        // concede ao botão um identificador de tipo único
        killHypStateBtn.setId("killHypStateBtn" + currentState);
        
        //Concede ao botão a capacidade de lidar com "clicks" recebidos
        killHypStateBtn.addEventHandler
        (   
            MouseEvent.MOUSE_CLICKED,
            new EventHandler<MouseEvent>() 
            {
                @Override public void handle(MouseEvent e)
                {
                    removeHypState(e); //método executado quando o botão é clickado
                }
            }
        );
        
        //adiciona os componentes criados na ordem em que deverão aparecer em uma linha da tabela do nodo de hipótese
        rowComponents[0] = hypStateNameTxtField ;
        rowComponents[1] = hypStateProbTxtField ;
        rowComponents[2] = killHypStateBtn ;  
                  
        return rowComponents;
    }
    
    @FXML   // Destaca o botão de exclusão do nodo de hipótese quando o mouse passa sobre ele
    private void closeButtonHighlightOn()
    {
        this.hypNode_closeBtn.getStyleClass().clear();
        this.hypNode_closeBtn.getStyleClass().add("hyp-title-fonts");
        this.hypNode_closeBtn.getStyleClass().add("close-button-highlight-on");
    }
    
    @FXML // Remove o destaque do botão de exclusão do nodo de hipótese quando o mouse deixa de passar sobre ele
    private void closeButtonHighlightOff()
    {
        this.hypNode_closeBtn.getStyleClass().clear();
        this.hypNode_closeBtn.getStyleClass().add("hyp-title-fonts");
        this.hypNode_closeBtn.getStyleClass().add("close-button-highlight-off");
    }

    @FXML // Deve apagar uma linha inteira do nodo de hipótese após o botão referente a ela ser clicado
    private void removeHypState(Event event)
    {
        // Acessa o botão que originou o evento de exclusão do estado da hipótese
        Button btn = (Button) event.getSource();
        
        // Acessa o grid ao qual o botão clickado pertence (tabela de estados)
        GridPane grid = (GridPane) btn.getParent();
        
        // Informa a linha do grid à qual o botão clickado pertence
        int numRow = this.hypNode_statesGrid.getRowIndex(btn);
        
        // Deleta do grid a linha referente ao botão clickado
        this.deleteRow(this.hypNode_statesGrid, numRow);
        
//        System.out.println( "Id do botão clickado: " + btn.getId());
//        System.out.println( "Id do GridPane clickado: " + grid.getId());
//        System.out.println( "Linha do botao clickado: " + numRow);
//        System.out.println( "Linha apagada: " + numRow);
//        int numChildren = this.hypNode_statesGrid.getChildren().size(); //retorna a quantidade total de nodos filhos do grid
//        System.out.println( "Quantidade de filhos do grid: " + numChildren);
        
        //this.updatePromptTexts(this.hypNode_statesGrid) // atualiza os textos "default" dos campos de texto após uma linha ser apagada
    }
    
    //  Apaga uma linha específica de um determinado gridPane
    static void deleteRow(GridPane grid, final int row)
    {
        Set<Node> deleteNodes = new HashSet<>();    // reúne os nodos que serão excluídos
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
    
    @FXML // Botão "OK" do nodo de hipótese
    public void lockHypNode()  // Método que realiza bloqueio/desbloqueio do nodo de hipótese
    {
        // Verifica se o nodo está "locked" (sem permitir edição)
        if ( this.isLocked() ) 
        {
            // É feito o "unlock" do nodo, para permitir edição de seus conteúdos
            this.setLocked(false);
            
            // Desabilita a criação de evidências enquanto o nodo de hipótese está sendo editado
            self.mainControl.btnCreateEvidNode.setDisable(true);
            
            // Libera os componentes do nodo de hipótese para edição
            this.hypNode_nodeNameTxtField.setDisable(false);
            this.hypNode_statesGrid.setDisable(false);
            this.hypNode_addStateBtn.setDisable(false);
            this.btn_lockHypNode.setDisable(false);
        }
        // Quando é solicitado o bloqueio do nodo
        else // Nesse caso, o nodo ainda não está "locked", então deve-se bloquear a edição
        {
            // É definido o estado "locked" para o nodo, para impedir a edição de seus conteúdos
            this.setLocked(true);
            
            // Bloqueia a edição dos componentes do nodo de hipótese
            this.hypNode_nodeNameTxtField.setDisable(true);
            this.hypNode_statesGrid.setDisable(true);
            this.hypNode_addStateBtn.setDisable(true);
            
            // Verifica se o nodo foi preenchido corretamente
            // Caso haja algum problema, o mesmo será descrito na barra de notificações
            boolean correctNode = this.isHypNodeComplete() ;
            
            // Se não houver problemas, então o nodo é bloqueado
            if ( correctNode )
            {
                // Habilita a criação de nodos de evidência, apenas se o nodo de hipótese estiver corretamente preenchido
                self.mainControl.btnCreateEvidNode.setDisable(false);
                
//                this.quantHypStates = this.getRowCount(this.hypNode_statesGrid) ; // define a quantidade de estados contidos na hipótese
                
                //Impede que o nodo de hipótese seja editado enquanto se criam evidências
//                self.setDisabled(true);
            }
        }
    }
    
    // Método que informa a quantidade de linhas (estados) presente no nodo de hipótese
    public int getCountHypStates ()
    {
        return this.getRowCount(this.hypNode_statesGrid) ; // conta as linhas do grid - cada linha é 1 estado
    }
    
    // verifica se todos os campos do nodo de hipótese foram preenchidos
    // apenas permitirá a criação de nodos de evidência caso o retorno seja "true"
    public boolean isHypNodeComplete()
    {
        if ( this.hypNode_nodeNameTxtField.getText().isEmpty() ) // verifica se o nome do nodo foi adicionado
        {
            String erro = "ERRO: Hipótese sem nome! Todo nodo de hipótese deve possuir um nome!" ;
            this.mainControl.notifyError(erro);
            System.out.println (erro);
            return false;
        }
        else // Caso o erro encontrado tenha sido corrigido
        {
            this.mainControl.clearStyleErrorTab(); // Remove a mensagem de erro
        }
        
        if ( this.getRowCount(this.hypNode_statesGrid) < 1 ) // verifica se pelo menos 1 estado foi adicionado
        {
            String erro = "ERRO: Nenhum estado adicionado à hipótese! O nodo de hipótese deve possuir ao menos 1 estado!";
            System.out.println (erro);
            this.mainControl.notifyError(erro);
            return false;
        }
        else // Caso o erro encontrado tenha sido corrigido
        {
            this.mainControl.clearStyleErrorTab(); // Remove a mensagem de erro
        }
        
        for (Node child : self.mainControl.getAllNodes(this)) // percorre todos os nodos do grid para ver se todos foram preenchidos
        {
//            System.out.println ( "\n >>> ID do nodo atual: " + child.getId() + " / StyleType:" + child.getStyleClass());
            if ( child instanceof TextField ) // verifica se "child" é um texField referente a nome de estado
            {
                TextField child2 = (TextField) child ;      // cria uma copia do textField atual
                
//                System.out.println ( "Texto lido no textField: " + child2.getText() );
                
                if ( child2.getText().isEmpty() )
                {                    
                    String erro = "ERRO: 1 ou mais campos de texto não preenchido(s) no nodo de hipótese! Todos os campos devem ser preenchidos antes de continuar!";
                    System.out.println (erro);
                    this.mainControl.notifyError(erro);                    
                    return false ;
                }
                else // Caso o erro encontrado tenha sido corrigido
                {
                    this.mainControl.clearStyleErrorTab();  // Remove a mensagem de erro
                }
            }
        }
        
        int numOfStates = this.getRowCount(this.hypNode_statesGrid);    // Verifica o número de linhas/estados do gridpane
        int numOfCols = this.getColCount(this.hypNode_statesGrid) ;     // Verifica o número de colunas do gridpane

        // Percorre cada coluna do gridpane, exceto a última (que possui apenas botões)
        for(int i = 1; i <= numOfCols-2 ; i++)
        {
//            double colSum = 0 ; // Guarda a soma das probs de uma coluna
            float colSum = 0f ; // Guarda a soma das probs de uma coluna

            // Percorre as linhas da coluna atual atrás de cada valor de probabilidade o agrega ao colSum
            for (int j = 0; j <= numOfStates-1 ; j++)
            {
                // Armazena o campo de texto atual para extrair o valor de seu texto
                TextField childText = (TextField) this.getNodeByRowColumnIndex(j, i, this.hypNode_statesGrid) ;
                String input = childText.getText();
                
//                double prob = Double.parseDouble(input) ;   // Conversão do valor textual para numérico
//                colSum += prob ;                            // Agrega o valor lido à soma atual da coluna corrente

                float prob = Float.parseFloat(input) ;   // Conversão do valor textual para numérico
                colSum += prob ;                            // Agrega o valor lido à soma atual da coluna corrente

                
//                System.out.println ( "\n >>> Valor do nodo atual: " + input);                
//                System.out.println ("\n>>> COLSUM = " + colSum);
            }
            
            // Se a soma das probabilidades em uma coluna não for igual a "1", é apontado um erro
            if ( colSum != 1)
            {
                String erro = "ERRO: A Soma das probabilidades de cada coluna deve ser 1!";
                System.out.println (erro);
                this.mainControl.notifyError(erro);                    
                return false ;
            }
            else // Nesse caso, o nodo de hipótese foi preenchido de forma correta
            {
                this.mainControl.clearStyleErrorTab();  // Apagam-se as mensagens de erro
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
    
    // Cria uma janela personalizada que requisita confirmação do usuário
    public boolean warningHypRemoval()
    {
        // https://stackoverflow.com/questions/28417140/styling-default-javafx-dialogs
        // https://stackoverflow.com/questions/26808261/action-buttons-css-style-in-javafx-controlfx-dialog
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Atenção!");
        alert.setHeaderText("A exclusão de um nodo de hipótese irá remover também" +
                            "\ntodos os nodos de evidência.");
        alert.setContentText("Deseja continuar mesmo assim?");
        
        // Customizando estilo css do dialog pane
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
        getClass().getResource("/userInterface/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-hyp-removal");
        
        // custom icon
        StackPane stackPane = new StackPane(new ImageView(
                new Image(getClass().getResourceAsStream("/userInterface/alert-icon.png")) {}));
        stackPane.setPrefSize(24, 24);
        stackPane.setAlignment(Pos.CENTER);
        dialogPane.setGraphic(stackPane);
        
        // Editando estilos dos botões
        ButtonBar buttonBar = (ButtonBar)alert.getDialogPane().lookup(".button-bar");
//        buttonBar.setStyle("-fx-font-size: 24px;"
//                + "-fx-background-color: indianred;");
//        buttonBar.getButtons().forEach(b->b.getStyleClass().clear());
        
        buttonBar.getButtons().forEach(b->b.getStyleClass().add("dialog-hyp-removal-btn")) ;
       
//        buttonBar.getButtons().forEach(b->b.setStyle("-fx-font-family: \"Andalus\";" + 
//                                                     "-fx-background-color:  #0205ca ;"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
        {
            return true ;
        } 
        else
        {
            return false ;
        }
    }
    
    // Desabilita a exclusão do nodo de hipótese
    public void lockHypExclusion()
    {
        this.hypNode_closeBtn.setDisable(true);
    }
    
    // Reabilita a exclusão do nodo de hipótese
    public void unlockHypExclusion()
    {
        this.hypNode_closeBtn.setDisable(false);
    }
    
    // Assimila os inputs fornecidos nos campos do nodo de hipótese
    public void computeHypInputs()
    {
        // Cria o novo objeto de hipótese
        this.hypothesis = new Hypothesis();
        
        // Associa o nome do nodo de Hipótese
        this.hypothesis.setNodeName(this.hypNode_nodeNameTxtField.getText());
        
        int numOfStates = this.getRowCount(this.hypNode_statesGrid);    // Verifica o número de linhas/estados do gridpane
        int numOfCols = this.getColCount(this.hypNode_statesGrid) ;     // Verifica o número de colunas do gridpane
        
        // Percorre cada linha do gridpane para criar os estados (cada linha é um estado)
        for(int row = 0; row <= numOfStates-1 ; row++)
        {
            HypothesisState hypState = new HypothesisState() ; // Objeto que representa um estado da hipótese
            
            // Percorre cada coluna do gridpane (exceto a última, que só tem botões)
            for(int col = 0; col <= numOfCols-2 ; col++)
            {
                // Armazena o campo de texto atual para extrair o valor de seu texto
                TextField childText = (TextField) this.getNodeByRowColumnIndex(row, col, this.hypNode_statesGrid) ;
                String input = childText.getText();
                
                // Verifica se é o nome do estado
                if ( childText.getId().equals(this.HYP_STATE_NAME) )
                {
                    hypState.setNameHypState(input);
                }
                // Se não for um nome de estado, espera-se que seja uma probabilidade
                else if ( childText.getId().equals(this.HYP_STATE_PROB) )
                {
                    float prob = Float.parseFloat(input) ;  // Converte o texto de entrada para um valor numérico
                    hypState.setProb_Hyp(prob);             // Adiciona a prob lida ao seu estado correspondente
                }                
            }
            // Adiciona o estado recém-lido na hipótese
            this.hypothesis.addHypState(hypState);
            
//            System.out.println("\n\nEstado adicionado à " + this.hypothesis.showStates());
            
//            
//            System.out.println("\n\nEstado adicionado à Hipótese \"" + this.hypothesis.getNodeName() + "\": " +
//                                this.hypothesis.getHypStates().get(row).getNameHypState());
//            System.out.println("\n\nQuantidade de estados na hipótese criada: " +
//                                this.hypothesis.getHypStates().size());
        }       
    }
    
}
