package app;

import static app.FunctionKind.*;
import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
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
import javafx.scene.control.ComboBox;

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
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import jfxtras.labs.util.event.MouseControlUtil;

public class FuzzyNode extends AnchorPane
{
    public Evidence evidence ;     // Objeto evidência que é representado por este nodo
    
    public FuzzyEvid fuzzyEvid ;
    
    private MainScreen mainControl ;
    private final FuzzyNode self;
    
    private boolean locked ;        // armazena o estado atual do nodo quanto à sua editabilidade (true = não editável, false = editável)  
    
    public static final String FUZZY_STATE_NAME = "fuzzyStateName" ;
    public static final String FUNCTION_SELECTOR = "functionType";
    public static final String FUZZY_STATE_POINT = "fuzzyStatePoint" ;
    public static final String FUZZY_ADD_POINT_BTN = "fuzzyAddPointButton" ;
    
    // Nomes expositivos dos tipos de função de pertinência aplicáveis aos conjuntos fuzzy (estadosFuzzy)
    public static final String FUNC_NAME_DEFAULT = "Default" ;  // Se for default, permite a criação de 2 ou mais pontos
    public static final String FUNC_NAME_TRIAN = "Triangular" ;      // Se for "trian", de triangular, representa uma função com apenas 3 pontos
    public static final String FUNC_NAME_TRAPE = "Trapezoidal" ;      // Se for "trape", de trapezoidal, representa uma função com apenas 4 pontos
    public static final String FUNC_NAME_GAUSS = "Gaussiana" ;      // Se for "gauss", de gaussiana, representa uma função com apenas 2 pontos
    public static final String FUNC_NAME_GBELL = "Sino" ;      // Se for "gbell", de sino, representa uma função com apenas 3 pontos
    public static final String FUNC_NAME_SIGM = "Sigmoidal" ;        // Se for "sigm", de sigmoidal, representa uma função com apenas 2 pontos
    
    // Placeholder para o tipo de função
    public static final String CHOOSE_FUNC_TYPE = "Tipo de Função" ; 
    
    public ArrayList<FunctionKind> functionKinds = new ArrayList<FunctionKind>();    
    public final FuzzyFileFormatter fuzzyFormatter = new FuzzyFileFormatter(); 
    
    @FXML private AnchorPane fuzzyNode_rootPane;
    
    @FXML private VBox fuzzyNode_contentVBox;
    
    @FXML private HBox fuzzyNode_titleHBox;
    @FXML private Label fuzzyNode_lblNodeName;           // Representa o NOME do nodo, e também é a área clicável que permite a movimentação do nodos
    @FXML private HBox fuzzyNode_HboxGrids;
    
    @FXML private GridPane fuzzyStatesGrid ;     // Grid com estados da evidência e suas opções de funções de pertinência
//    @FXML private GridPane fuzzyPointsGrid;         // tabela de pontos dos estados
    
    @FXML private HBox fuzzyNode_okHbox;
    @FXML private Button btn_lockFuzzyNode ;
    @FXML private Tooltip tooltip_lockFuzzyNode ;
    
    
    //Cria o controlador do nodo de hipótese e o associa ao .fxml correspondente
    public FuzzyNode() 
    {
        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("/userInterface/FuzzyNode.fxml") );
        fxmlLoader.setRoot(this); 
        fxmlLoader.setController(this);
        self = this;
        this.setFunctionTypes();
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
    
    public void setFunctionTypes()
    {
        this.functionKinds = this.fuzzyFormatter.functionDefinitions ;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    public FuzzyEvid getFuzzyEvid() {
        return fuzzyEvid;
    }

    public void setFuzzyEvid(FuzzyEvid fuzzyEvid) {
        this.fuzzyEvid = fuzzyEvid;
    }
    
    
    
    
     public boolean isLocked()
    {
        return this.locked;
    }

    public void setLocked(boolean lockState)
    {
        this.locked = lockState;
    }        

    public MainScreen getMainControl()
    {
        return mainControl;
    }
    
    public void setMainControl (MainScreen control)
    {
        this.mainControl = control ;
    }
    
    public String getFuzzyNodeName()
    {
        return this.fuzzyNode_lblNodeName.getText();
    }
    
//    public ArrayList<String> getStateNames()
//    {
//        
//    }
    
    @FXML // apaga um nodo de hipotese
    private void removeFuzzyNode() 
    {
        // Aponta para o contêiner imediato do nodo fuzzy e remove a si mesmo dele
        AnchorPane parent  = (AnchorPane) self.getParent();        
        parent.getChildren().remove(self);        
    }

    @FXML // adiciona um novo estado ao nodo de hipotese
    private void addEvidState() 
    {
//        int numRows = this.getRowCount(evidNode_statesGrid); // conta o numero de linhas no grid (tabela de estados da evid)
//        int numCols = this.getColCount(evidNode_statesGrid); // conta o numero de colunas no grid (tabela de estados da hipotese)
//        
//        Node[] rowComponents = this.evidRowSetup(numRows); // monta uma linha "padronizada" para adicionar ao nodo de hipótese
//        int lastRowIndex = rowComponents.length-1 ;       // aponta para o índice do botão na tabela, para ajustá-lo na inserção no grid
//        
//        this.evidNode_statesGrid.addRow(numRows, rowComponents );    // adiciona a linha "padronizada" ao grid do nodo
//        this.evidNode_statesGrid.setColumnIndex(rowComponents[0], 0);// atribui explicitamente a posição "0" referente aos itens da coluna 0
////        GridPane.setColumnIndex(rowComponents[0], 0) ;
//        this.evidNode_statesGrid.setHalignment(rowComponents[lastRowIndex], HPos.RIGHT); // define o ajuste de alinhamento adequado para o botão dentro de sua célula
//     
        
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
    
    // Prepara uma variável fuzzy com base nas evidências bayesianas já criadas
    public void fuzzyNodeSetup()
    {
        Evidence evid = this.evidence ;
        this.fuzzyStatesGrid.setHgap(2);
        this.fuzzyStatesGrid.setVgap(2);
        this.fuzzyNode_lblNodeName.setText(evid.getNodeName());
        
        // Percorre todos os estados da evidência para inicializar as colunas contendo nomes de estados e tipos de funçãos
        for ( EvidenceState state : evid.getEvidenceStates())
        {
            Node[] stateAndFunction = new Node[6]; //2 (deve armazenar o nome do estado e tipo de função de pertinência)
            
            // Cria Lable com o nome do estado
            Label stateName = new Label(state.getNameEvidState());
            stateName.setMinWidth(90);                              // Seta o tamanho mínimo da visualização            
            stateName.getStyleClass().add("fuzzy-state-label");      // Adiciona classe de estilo .css à label
            stateName.setId(FUZZY_STATE_NAME);                      // Concede identificador de tipo único ao nodo
            stateAndFunction[0] = stateName ;                       // Insere a label na primeira posição a aparecer na linha
            
            // Armazena os tipos de função a serem ofertados para escolha na ComboBox
            ArrayList<String> nameFuncs = new ArrayList<String>() ;
            for( FunctionKind func : this.functionKinds )
            {
                nameFuncs.add(func.getNameFunc()) ;         // Referencia apenas o nome que deve aparecer na lista da ComboBox
            }                    
            
            // Conta a quantidade de estados já adicionados ao fuzzyNode            
            int rowCount = this.getRowCount(fuzzyStatesGrid) ;
            
            // Cria a ComboBox de escolha do tipo de função de pertinência
            ComboBox typeSelector = new ComboBox();            
            typeSelector.getItems().setAll(nameFuncs);        // Adiciona os tipos de função à ComboBox
//            typeSelector.setPromptText(CHOOSE_FUNC_TYPE);
            typeSelector.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
            typeSelector.getSelectionModel().selectFirst();   // Define a opção inicial do seletor como "default", para ser mostrada na ComboBox
            typeSelector.setId(FUNCTION_SELECTOR + "-" + rowCount);            // Concede um identificador de tipo único ao nodo
            typeSelector.getStyleClass().add("fuzzy-state-function-selector"); // Adiciona classe de estilo .css à label

            // Adiciona o listener à comboBox para tratar a escolha de cada tipo de função
            typeSelector.valueProperty().addListener
            (
                new ChangeListener<String>()
                {
                    @Override public void changed(ObservableValue ov, String t, String t1)
                    {
                        self.comboSelectedValue( ov, t, t1);
                    }    
                }
            );


            stateAndFunction[1] = typeSelector ;              // Insere a ComboBox na segunda posição da linha 
            
            
            for (int i = 2 ; i < 6 ; i++)
            {
                //cria o textField de um ponto do novo estado
                TextField fuzzyPoint = new TextField();  //
                fuzzyPoint.setPromptText("X");        
                fuzzyPoint.getStyleClass().add("fuzzy-state-point-field");
                fuzzyPoint.setTextFormatter(this.formatNumericTextField()); //adiciona o filtro de formato numérico ao textField

                // concede ao textField da prob do novo estado um identificador de tipo único
                fuzzyPoint.setId(FUZZY_STATE_POINT);

//                fuzzyPoint.setPrefHeight(27);
//                fuzzyPoint.setMaxHeight(USE_PREF_SIZE);
                
                fuzzyPoint.setMinWidth(50);

    //            evidStateNameTxtField.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
                stateAndFunction[i] = fuzzyPoint ;
            }
            
            // Adiciona a nova linha (NomeDoEstado + FunctionSelector) ao fuzzyNode
            this.fuzzyStatesGrid.addRow(rowCount, stateAndFunction);
            this.fuzzyStatesGrid.setColumnIndex(stateAndFunction[0], 0);   // Ajusta os parâmetros de índices dos nodos no gridPane
            this.fuzzyStatesGrid.setHalignment(stateAndFunction[0], HPos.CENTER);
            this.fuzzyStatesGrid.setValignment(stateAndFunction[0], VPos.CENTER);
            
            // Ajusta os parâmetros de índices dos nodos no gridPane
            if ( this.getRowCount(fuzzyStatesGrid) == 1)
            {
                this.fuzzyStatesGrid.setRowIndex(stateAndFunction[0], 0);
                this.fuzzyStatesGrid.setRowIndex(stateAndFunction[1], 0);
            }
            
            // TESTE - comentário de teste
//            ComboBox combo = (ComboBox) this.getNodeByRowColumnIndex(0, 1, stateAndFunctionGrid);
//            System.out.println(" //////////////// state/function row count: " + this.getRowCount(stateAndFunctionGrid));
//            System.out.println(" //////////////// combo selection: " + combo.getSelectionModel().getSelectedItem().toString());
        }
        // Conta a quantidade de estados adicionados ao fuzzyNode            
//        int rowCount = this.getRowCount(stateAndFunctionGrid) ;
//        
//        
//        // Add one RowConstraint for each row. The problem here is that you
//        // have to know how many rows you have in you GridPane to set
//        // RowConstraints for all of them.
//        for (int i = 0; i <= rowCount ; i++)
//        {
//            Label l = new Label ("") ;
////            RowConstraints con = new RowConstraints();
////            // Here we set the pref height of the row, but you could also use .setPercentHeight(double) if you don't know much space you will need for each label.
////            con.setPrefHeight(60);
//            this.fuzzyPointsGrid.add(l, 0, i);
//        }
    }
    
    
     public void defineFunctionType (int row, String funcName)
    {
        // Neste ponto, terminou de criar as colunas com os estados e os seletores de tipo de função
        // A seguir, deve criar o restante das linhas, com os textFields de pontos (e botão, se o tipo da função for default)
        
        // Reserva os nodos atuais desta linha para remover depois de adicionar os novos 
//        Set<Node> nodesToRemove = this.nodesToDelete(fuzzyPointsGrid, row) ;

        // Quantidade esperada de pontos
        int expectedLockedPoints = 0 ;

        // Altera a quantidade esperada de pontos de acordo com o tipo de função escolhida
        switch (funcName)
        {
            case FUNC_NAME_DEFAULT :
            {
                expectedLockedPoints = 4-2 ;
                this.lockFuzzyPoints ( row, expectedLockedPoints);
                break;
            }
            case FUNC_NAME_TRIAN :
            {
                expectedLockedPoints = 4-3 ;
                this.lockFuzzyPoints ( row, expectedLockedPoints);
                break;
            }
            case FUNC_NAME_TRAPE :
            {
                expectedLockedPoints = 4-4 ;
                this.lockFuzzyPoints ( row, expectedLockedPoints);
                break;
            }
            case FUNC_NAME_GAUSS :
            {
                expectedLockedPoints = 4-2 ;
                this.lockFuzzyPoints ( row, expectedLockedPoints);
                break;
            }
            case FUNC_NAME_GBELL :
            {
                expectedLockedPoints = 4-3 ;
                this.lockFuzzyPoints ( row, expectedLockedPoints);
                break;
            }
            case FUNC_NAME_SIGM :
            {
                expectedLockedPoints = 4-2 ;
                this.lockFuzzyPoints ( row, expectedLockedPoints);
                break;
            }
            default :
                break;
//        }
//
////        Node[] functionPoints ;
//        // Verifica se o tipo de função é default (vai ser deixado de lado por enquanto 
//        int numPoints = expectedLockedPoints ;
//        if ( funcName.equals(FUNC_NAME_DEFAULT) )
//        {
//            // Se for default, adiciona um espaço a mais para criação do botão de adição de ponto        
//            numPoints++;
//            functionPoints = new Node[numPoints];
//
//            for (int i = 0 ; i < functionPoints.length-1 ; i++)
//            {
//                //cria o textField da prob do novo estado
//                TextField fuzzyPoint = new TextField();  //
//                fuzzyPoint.setPromptText("(X,Y)");        
////                fuzzyPoint.getStyleClass().add("textField-node");
//                fuzzyPoint.setTextFormatter(this.formatNumericTextField()); //adiciona o filtro de formato numérico ao textField de probabilidades
//
//                // concede ao textField da prob do novo estado um identificador de tipo único
//                fuzzyPoint.setId(FUZZY_STATE_POINT);
//
//    //            evidStateProbTxtField.setPadding(Insets.EMPTY);
//                fuzzyPoint.setMinWidth(50);
//
//    //            evidStateNameTxtField.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
//                functionPoints[i] = fuzzyPoint ;
//            }
//
//            //cria o botão de adição de novo ponto
//            Button addPointBtn = new Button();  //
//            addPointBtn.setText("+");        
////            addPointBtn.getStyleClass().add("btn-kill-hyp-state");
//            addPointBtn.setPickOnBounds(false); // permite clickar apenas na área visível do botão
//
//            // concede ao botão um identificador de tipo único
//            addPointBtn.setId("addStatePointBtn");
//
//            //Concede ao botão a capacidade de lidar com "clicks" recebidos
//            addPointBtn.addEventHandler
//            (   
//                MouseEvent.MOUSE_CLICKED,
//                new EventHandler<MouseEvent>() 
//                {
//                    @Override public void handle(MouseEvent e)
//                    {
////                        removeEvidState(e); //método executado quando o botão é clickado
//                        //addStatePoint (e) ;
//                    }
//                }
//            );
//
//            int lastPos = functionPoints.length-1 ;       // aponta para a última coluna
//            functionPoints[lastPos] = addPointBtn ;  // adiciona o último componente da linha (botão de exclusão de estado)
//        }
//        else
//        {
//            // Guarda os textFields referentes a criação dos pontos antes de adicioná-los ao FuzzyNode
//            functionPoints = new Node[numPoints];
//
//            for (int i = 0 ; i < numPoints ; i++)
//            {
//                //cria o textField da prob do novo estado
//                //cria o textField da prob do novo estado
//                TextField fuzzyPoint = new TextField();  //
//                fuzzyPoint.setPromptText("x");        
////                fuzzyPoint.getStyleClass().add("textField-node");
//                fuzzyPoint.setTextFormatter(this.formatNumericTextField()); //adiciona o filtro de formato numérico ao textField de probabilidades
//
//
//                // concede ao textField da prob do novo estado um identificador de tipo único
//                fuzzyPoint.setId(FUZZY_STATE_POINT);
////                fuzzyPoint.setPadding(Insets.EMPTY);
//                fuzzyPoint.setMinWidth(50);
//
//    //            evidStateNameTxtField.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
//                functionPoints[i] = fuzzyPoint ;
//            }
//        }                        
//
//        int rowCount = this.getRowCount(fuzzyPointsGrid) ;
//        this.fuzzyPointsGrid.addRow(row, functionPoints);
//        this.fuzzyPointsGrid.setColumnIndex(functionPoints[0], 0);
//
//        if ( this.getRowCount(fuzzyPointsGrid) == 1)
//        {
//            this.fuzzyPointsGrid.setRowIndex(functionPoints[0], 0);
//            this.fuzzyPointsGrid.setRowIndex(functionPoints[1], 0);
//        }
//        
//        this.removeSpecificNodes(fuzzyPointsGrid, nodesToRemove); // remove os nodos defasados desta linha
////
//        ComboBox combo = (ComboBox) this.getNodeByRowColumnIndex(0, 1, stateAndFunctionGrid);
//        System.out.println(" //////////////// state/function row count: " + this.getRowCount(stateAndFunctionGrid));
//        System.out.println(" //////////////// combo selection: " + combo.getSelectionModel().getSelectedItem().toString());             
        }
    }
//    
     
    public void lockFuzzyPoints (int row, int numOfLocks)
    {
        //SE FOR UTILIZAR AS 2 LINHAS ABAIXO, DEVE-SE EDITAR O MÉTODO "isFuzzyNodeComplete()" para procurar por "isDisabled()" em vez de "isVisible()"
//        this.getNodeByRowColumnIndex(row, 4, fuzzyStatesGrid).setDisable(false); // simplesmente destranca o campo
//        this.getNodeByRowColumnIndex(row, 5, fuzzyStatesGrid).setDisable(false); // simplesmente destranca o campo
        
        this.getNodeByRowColumnIndex(row, 4, fuzzyStatesGrid).setVisible(true); // Habilita um campo ao torna-lo visível
        this.getNodeByRowColumnIndex(row, 5, fuzzyStatesGrid).setVisible(true); // Habilita um campo ao torna-lo visível
        
        int lastToLock = 5-numOfLocks ;
        for ( int i = 5 ; i > lastToLock ; i-- )
        {
            this.getNodeByRowColumnIndex(row, i, fuzzyStatesGrid).setVisible(false);    // Desabilita um campo ao torna-lo invisível
            
            //SE FOR UTILIZAR A LINHA ABAIXO, DEVE-SE EDITAR O MÉTODO "isFuzzyNodeComplete()" para procurar por "isDisabled()" em vez de "isVisible()"
//            this.getNodeByRowColumnIndex(row, i, fuzzyStatesGrid).setDisable(true); // simplesmente tranca o campo (mas ainda o deixa visível)            
            
            TextField t = (TextField) this.getNodeByRowColumnIndex(row, i, fuzzyStatesGrid);
            t.setText(null);
//            fuzzyStatesGrid.re
        }
        
    }
    
    @FXML
    private void lockFuzzyNode()
    {
        // Verifica se o nodo está "locked" (sem permitir edição)
        if ( this.isLocked() )
        {
            // É feito o "unlock" do nodo, para permitir edição de seus conteúdos
            this.setLocked(false);
            this.btn_lockFuzzyNode.setDisable(false);
            this.fuzzyStatesGrid.setDisable(false);
            
            // Verifica se a opção de compilação da rede não está habilitada
            if ( !this.mainControl.btnCompileFuzzyInputs.isDisable() )
            {
                // Se o botão de compilação da rede estiver habilitado, então deve-se desativá-lo
                this.mainControl.btnCompileFuzzyInputs.setDisable(true);
            }
        }
        else // Se o nodo de evidência está editável
        {
            // É feito o "lock" do nodo, para impedir a edição de seus conteúdos
            this.setLocked(true);
            
            this.fuzzyStatesGrid.setDisable(true);;
            
            if ( this.isFuzzyNodeComplete())
            {
//                self.mainControl.btnCreateEvidNode.setDisable(false); //habilita a criação de evidências
                
                // Verifica se as demais evidências estão corretas
                boolean evidsCorrect = self.mainControl.isWithAllFuzzyNodesComplete();
                if ( evidsCorrect )
                {
                    // Se as demais evidências estiverem corretas, habilita a opção de compilação da rede
                    self.mainControl.btnCompileFuzzyInputs.setDisable(false);
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

            this.fuzzyStatesGrid.setDisable(true);
//            this.evidNode_statesGrid.setDisable(true);
//            this.evidNode_addStateBtn.setDisable(true);            
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
        return this.evidence.getEvidenceStates().size() ; // conta as linhas do grid - cada linha é 1 estado
    }
    
    // Verifica se todos os campos do nodo de evidência foram preenchidos
    // Apenas permitirá a criação de nodos de evidência caso o retorno seja "true"
    public boolean isFuzzyNodeComplete()
    {
        for (Node child : self.mainControl.getAllNodes(this)) // percorre todos os nodos do grid
        {
//            System.out.println ( "\n >>> ID do nodo atual: " + child.getId() + " / StyleType:" + child.getStyleClass());
            if ( child instanceof TextField ) // verifica se "child" é um texField referente a nome de estado
            {
                TextField child2 = (TextField) child ;      // cria uma copia do textField atual
//                System.out.println ( "Texto lido no textField: " + child2.getText() );
//                if ( !child2.isDisable() && child2.getText().isEmpty() )
                if ( child2.isVisible() && (child2.getText() == null || child2.getText().isEmpty()) )
                {
                    String evidName = this.evidence.getNodeName();
                    String erro = "ERRO~FUZZY: 1 ou mais campos de texto não preenchido(s) na evidência \"" 
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
        return true ;
    }
    
    
//    private void lockFuzzyNode()
//    {
//        // Verifica se o nodo está "locked" (sem permitir edição)
//        if ( this.isLocked() )
//        {
//            // É feito o "unlock" do nodo, para permitir edição de seus conteúdos
//            this.setLocked(false);
//            
////            self.mainControl.btnCreateEvidNode.setDisable(true); // desabilita a criação de evidências
//            this.fuzzyStatesGrid.setDisable(false);
//            
////            // Verifica se a opção de compilação da rede não está habilitada
//            if ( !this.mainControl.btnCompileFuzzyInputs.isDisable() )
//            {
//                // Se o botão de compilação da rede estiver habilitado, então deve-se desativá-lo
//                this.mainControl.btnCompileFuzzyInputs.setDisable(true);
//            }
//        }
//        else // Se o nodo de evidência está editável
//        {
//            // É feito o "lock" do nodo, para impedir a edição de seus conteúdos
//            this.setLocked(true);
//            
//            this.fuzzyStatesGrid.setDisable(true);
//            
//            if ( this.isFuzzyNodeComplete())
//            {
////                self.mainControl.btnCreateEvidNode.setDisable(false); //habilita a criação de evidências
//                
//                // Verifica se as demais evidências estão corretas
//                boolean evidsCorrect = self.mainControl.isWithAllEvidsComplete();
//                if ( evidsCorrect )
//                {
//                    // Se as demais evidências estiverem corretas, habilita a opção de compilação da rede
//                    self.mainControl.btnCompileNetwork.setDisable(false);
//                }
//            }
//        }
//    }
    
    
    
    // Cria um formatador de texto para garantir apenas inputs numéricos nos textFields de probabilidades
    public TextFormatter formatNumericTextField ()
    {
//        TEM QUE VER ESSE FORMATO AQUI
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
        
    // Desabilita a edição do nodo de evidência
    public void lockFuzzyNodeEdition()
    {
        this.btn_lockFuzzyNode.setDisable(true);
    }
    
    // Reabilita a edição do nodo de evidência
    public void unlockFuzzyNodeEdition()
    {
        this.btn_lockFuzzyNode.setDisable(false);
    }
    
    // Método executado quando um tipo de função é escolhido em uma comboBox
    public void comboSelectedValue(ObservableValue comboProperties, String prevSelection, String newSelection)
    {
        int currentComboRow = this.extractNumberFromComboBoxId(comboProperties.toString()) ;
        
//        // TESTE COMBOBOX 
//        System.out.println( "\n******* TesteComboBox: Observable = " + comboProperties +
//                            "\n******* TesteComboBox: Observable.toString() = " + comboProperties.toString() +
//                            "\n\t * ID EXTRAIDO: " + this.extractNumberFromComboBoxId(comboProperties.toString()) +
//                            "\n\t * Previous Selection = " + prevSelection + 
//                            "\n\t * New Selection = " + newSelection);
//        
        this.defineFunctionType(currentComboRow, newSelection);
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
        
    }
    
    public void removeSpecificNodes (GridPane grid, Set<Node> nodesToKill )
    {
        grid.getChildren().removeAll(nodesToKill) ;
    }
    
    // Apaga uma determinada linha de um gridPane
    static Set<Node> nodesToDelete (GridPane grid, final int row) //  Apaga uma linha específica de um determinado gridPane
    {
        Set<Node> deleteNodes = new HashSet<>();    // reune os nodos que serão excluídos
        for (Node child : grid.getChildren())       // percorre todos os nodos do grid
        {            
            Integer rowIndex = GridPane.getRowIndex(child);     // get row index from current child
            
            int r = rowIndex == null ? 0 : rowIndex; // handle null values for index = 0

            if (r == row)
            {                
                deleteNodes.add(child);             // collect matching rows for deletion
            }
        }        
        return deleteNodes ;  // remove nodes from row
    }
    
        
     // Assimila os inputs fornecidos nos campos do nodo de evidência
    public void computeFuzzyInputs()
    {
        // Cria o novo objeto de evidência
        this.fuzzyEvid = new FuzzyEvid(this.evidence.getNodeName());
        this.fuzzyEvid.setFuzzyNode(this);
        
        ArrayList<FuzzyState> fuzzyStates = new ArrayList<FuzzyState>();
                     
        int numOfStates = this.getRowCount(this.fuzzyStatesGrid);    // Verifica o número de linhas/estados do gridpane
        int numOfCols = this.getColCount(this.fuzzyStatesGrid) ;     // Verifica o número de colunas do gridpane
        
        // Percorre cada linha do gridpane para criar os estados (cada linha é um estado)
        for(int row = 0; row <= numOfStates-1 ; row++)
        {
            FuzzyState fuzzyEvidState = new FuzzyState() ; // Objeto que representa um estado da evidencia fuzzy
            
            // Array temporario para armazenar os conjuntos de probabilidades de cada estado da evidencia
            // Probabilidades referentes a: P(e|Hi) = Probs_Evid_given_Hyp 
            ArrayList<Float> functionPoints = new ArrayList<Float>();    
            
            // Percorre cada coluna do gridpane
            for(int col = 0; col <= numOfCols-1 ; col++)
            {
                Node child = this.getNodeByRowColumnIndex(row, col, fuzzyStatesGrid);
                switch (col)
                {
                    case 0 :
                    {
                        Label stateName = (Label) child ;
                        fuzzyEvidState.setFuzzyStateName(stateName.getText());
                        break;
                    }
                    case 1 :
                    {
                        ComboBox combo = (ComboBox) child ;
                        String funType = combo.getSelectionModel().getSelectedItem().toString();
                        fuzzyEvidState.setFunctionType(funType);
                        break;
                    }
                    case 2 :
                    {
                        TextField statePoint = (TextField) child ;
                        // TESTE computeFuzzyInputs()
//                        System.out.println( "\n ++++++++++++ input apagado: " + statePoint.getText()) ;
                        if (statePoint.getText() != null)
                        {
                            float point = Float.parseFloat(statePoint.getText());
                            functionPoints.add(point);
                        }
                        break;
                    }
                    case 3 :
                    {
                        TextField statePoint = (TextField) child ;
                        // TESTE computeFuzzyInputs()
//                        System.out.println( "\n ++++++++++++ input apagado: " + statePoint.getText()) ;
                        if (statePoint.getText() != null)
                        {
                            float point = Float.parseFloat(statePoint.getText());
                            functionPoints.add(point);
                        }
                        break;
                    }
                    case 4 :
                    {
                        TextField statePoint = (TextField) child ;
                        // TESTE computeFuzzyInputs()
//                        System.out.println( "\n ++++++++++++ input apagado: " + statePoint.getText()) ;
                        if (statePoint.getText() != null)
                        {
                            float point = Float.parseFloat(statePoint.getText());
                            functionPoints.add(point);
                        }                        
                        break;
                    }
                    case 5 :
                    {
                        TextField statePoint = (TextField) child ;
                        // TESTE computeFuzzyInputs()
//                        System.out.println( "\n ++++++++++++ input apagado: " + statePoint.getText()) ;
                        if (statePoint.getText() != null)
                        {
                            float point = Float.parseFloat(statePoint.getText());
                            functionPoints.add(point);
                        }
                        break;
                    }
                    default :
                        break;
                }      
            }
            
            fuzzyEvidState.setFunctionPoints(functionPoints);
            fuzzyStates.add(fuzzyEvidState);            
        }
        this.fuzzyEvid.setFuzzyStates(fuzzyStates);        
        
        // TESTE computeFuzzyInputs()
//        System.out.println( "\n$$$ Evid:  "  + this.fuzzyEvid.getEvidName() + this.fuzzyEvid.showStates());
        //PRECISA SER INTEGRADO PARA CALCULO FUZZY-BAYES
        // Neste ponto, a evidência foi devidamente criada, então deve-se adicioná-la à hipótese da rede
//        this.mainControl.getHypNode().hypothesis.addEvidence(evidence);
        
    }
    
    @FXML
    private void removeFuzzyPoints(Event event) // deve apagar uma linha inteira do nodo de hipótese após o botão referente a ela ser clicado
    {       
//        // Acessa o botão que originou o evento de exclusão do estado da hipótese
//        Button btn = (Button) event.getSource();
//        
//        // Acessa o grid ao qual o botão clickado pertence (tabela de estados)
//        GridPane grid = (GridPane) btn.getParent();
//        
//        // Informa a linha do grid à qual o botão clickado pertence
//        int numRow = this.evidNode_statesGrid.getRowIndex(btn);
//        
//        // Deleta do grid a linha referente ao botão clickado
//        this.deleteRow(this.evidNode_statesGrid, numRow);
//        
//        System.out.println( "Id do botão clickado: " + btn.getId());
//        System.out.println( "Id do GridPane clickado: " + grid.getId());
//        System.out.println( "Linha do botao clickado: " + numRow);
//        System.out.println( "Linha apagada: " + numRow);
//        int numChildren = this.evidNode_statesGrid.getChildren().size();// Retorna a quantidade total de nodos filhos do grid
//        System.out.println( "Quantidade de filhos do grid: " + numChildren);
        
        //this.updatePromptTexts(this.hypNode_statesGrid) // atualiza os textos "default" dos campos de texto após uma linha ser apagada
    }
    
    // Método que serve para extrair o indicador de linha (int) a partir de um id (string) de comboBox (typeSelector)
    private int extractNumberFromComboBoxId (String comboId )
    {        
        Matcher matcher = Pattern.compile("\\d+").matcher(comboId);

        if (!matcher.find())
        {
            throw new NumberFormatException("For input string [" + comboId + "]");
        }
        
        return Integer.parseInt(matcher.group());    
    }
    
}


//    métdo antigo
//    public void defineFunctionType (String row, String funcName)
//    {
//        // Neste ponto, terminou de criar as colunas com os estados e os seletores de tipo de função
//        // A seguir, deve criar o restante das linhas, com os textFields de pontos (e botão, se o tipo da função for default)
//        for ( int row = 0 ; row < this.getRowCount(stateAndFunctionGrid) ; row++)
//        {
//            // Armazena o tipo de função para preparar os campos iniciais de input de pontos
//            ComboBox combo = (ComboBox) this.getNodeByRowColumnIndex(row, 1, stateAndFunctionGrid);
//            String functionKind = combo.getSelectionModel().getSelectedItem().toString();   // Obtém o valor selecionado na ComboBox
//            
//            // Quantidade esperada de pontos
//            int expectedNumOfPoints = 0 ;
//            
//            // Altera a quantidade esperada de pontos de acordo com o tipo de função escolhida
//            switch (functionKind)
//            {
//                case FUNC_NAME_DEFAULT :
//                {
//                    expectedNumOfPoints = 2 ;
//                    break;
//                }
//                case FUNC_NAME_TRIAN :
//                {
//                    expectedNumOfPoints = 3 ;
//                    break;
//                }
//                case FUNC_NAME_TRAPE :
//                {
//                    expectedNumOfPoints = 4 ;
//                    break;
//                }
//                case FUNC_NAME_GAUSS :
//                {
//                    expectedNumOfPoints = 2 ;
//                    break;
//                }
//                case FUNC_NAME_GBELL :
//                {
//                    expectedNumOfPoints = 3 ;
//                    break;
//                }
//                case FUNC_NAME_SIGM :
//                {
//                    expectedNumOfPoints = 2 ;
//                    break;
//                }
//                default :
//                    break;
//            }
//            
//            Node[] functionPoints ;
//            // Verifica se o tipo de função é default
//            // Se for default, adiciona um espaço a mais para criação do botão de adição de ponto
//            // Os campos de texto criados são diferentes também
//            int numPoints = expectedNumOfPoints ;
//            if ( functionKind.equals(FUNC_NAME_DEFAULT) )
//            {
//                numPoints++;
//                functionPoints = new Node[numPoints];
//                
//                
//                for (int i = 1 ; i<= numberOfHypStates ; i++)
//                {
//                    //cria o textField da prob do novo estado
//                    TextField evidStateProbTxtField = new TextField();  //
//                    evidStateProbTxtField.setPromptText("P(e" + currentState + "|H" + i + ")");        
//                    evidStateProbTxtField.getStyleClass().add("textField-node");
//                    evidStateProbTxtField.setTextFormatter(this.formatNumericTextField()); //adiciona o filtro de formato numérico ao textField de probabilidades
//
//                    // concede ao textField da prob do novo estado um identificador de tipo único
//                    evidStateProbTxtField.setId(this.EVID_STATE_PROB);
//
//        //            evidStateProbTxtField.setPadding(Insets.EMPTY);
//                    evidStateProbTxtField.setMinWidth(90);
//
//        //            evidStateNameTxtField.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
//                    rowComponents[i] = evidStateProbTxtField ;
//                }   
//                
//                
//                
//                
//                //cria o botão de remoção do novo estado
//                Button killEvidStateBtn = new Button();  //
//                killEvidStateBtn.setText("+");        
//                killEvidStateBtn.getStyleClass().add("btn-kill-hyp-state");
//                killEvidStateBtn.setPickOnBounds(false); // permite clickar apenas na área visível do botão
//
//                // concede ao botão um identificador de tipo único
//                killEvidStateBtn.setId("killHypStateBtn" + currentState);
//
//                //Concede ao botão a capacidade de lidar com "clicks" recebidos
//                killEvidStateBtn.addEventHandler
//                (   
//                    MouseEvent.MOUSE_CLICKED,
//                    new EventHandler<MouseEvent>() 
//                    {
//                        @Override public void handle(MouseEvent e)
//                        {
//                            removeEvidState(e); //método executado quando o botão é clickado
//                        }
//                    }
//                );
//
//                int lastPos = rowComponents.length-1 ;       // aponta para a última coluna
//                rowComponents[lastPos] = killEvidStateBtn ;  // adiciona o último componente da linha (botão de exclusão de estado)
//
//
//            }
//            else
//            {
//                // Guarda os textFields referentes a criação dos pontos antes de adicioná-los ao FuzzyNode
//                functionPoints = new Node[numPoints];
//
//                for (int i = 0 ; i < numPoints ; i++)
//                {
//                    //cria o textField da prob do novo estado
//                    TextField evidStateProbTxtField = new TextField();  //
//                    evidStateProbTxtField.setPromptText("P(e" + currentState + "|H" + i + ")");        
//                    evidStateProbTxtField.getStyleClass().add("textField-node");
//                    evidStateProbTxtField.setTextFormatter(this.formatNumericTextField()); //adiciona o filtro de formato numérico ao textField de probabilidades
//
//                    // concede ao textField da prob do novo estado um identificador de tipo único
//                    evidStateProbTxtField.setId(this.EVID_STATE_PROB);
//
//        //            evidStateProbTxtField.setPadding(Insets.EMPTY);
//                    evidStateProbTxtField.setMinWidth(90);
//
//        //            evidStateNameTxtField.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
//                    rowComponents[i] = evidStateProbTxtField ;
//                }
//            }                        
//            
//            int rowCount = this.getRowCount(stateAndFunctionGrid) ;
//            this.stateAndFunctionGrid.addRow(rowCount, stateAndFunction);
//            this.stateAndFunctionGrid.setColumnIndex(stateAndFunction[0], 0);
//            
//            if ( this.getRowCount(stateAndFunctionGrid) == 1)
//            {
//                this.stateAndFunctionGrid.setRowIndex(stateAndFunction[0], 0);
//                this.stateAndFunctionGrid.setRowIndex(stateAndFunction[1], 0);
//            }
//            
//            ComboBox combo = (ComboBox) this.getNodeByRowColumnIndex(0, 1, stateAndFunctionGrid);
//            System.out.println(" //////////////// state/function row count: " + this.getRowCount(stateAndFunctionGrid));
//            System.out.println(" //////////////// combo selection: " + combo.getSelectionModel().getSelectedItem().toString());
//        }     
//    }
////