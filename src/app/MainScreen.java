package app;

import com.sun.scenario.effect.impl.prism.PrImage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import jfxtras.labs.util.event.MouseControlUtil;
import javafx.scene.Node ;
import javafx.stage.Window;

public class MainScreen extends BorderPane
{
    @FXML Stage stage ;
    
    @FXML public BorderPane root_pane ;
    @FXML public VBox root_vBox ;
    @FXML private ToolBar tool_bar ;    
    
    @FXML public Button btnCreateHypNode ;   // Botão usado para criar um nodo de hipótese
    @FXML public Button btnCreateEvidNode ;  // Botão usado para criar um nodo de evidência
    @FXML public Button btnBayes ;
    @FXML public Button btnFuzzy ;
    @FXML public Button btnCompileNetwork ;
    @FXML public Button btnLoadNetwork ;
    
    @FXML public TabPane tabPane_areas ;
    
    @FXML public Tab tab_bayes ;
    @FXML public AnchorPane area_bayes ;     // Area em que os nodos são adicionados
    @FXML public Button btnAllowEditBayes;   // Botão para permitir a edição da rede bayesiana
    
    @FXML public Tab tab_fuzzy;
    @FXML public AnchorPane area_fuzzy ;
    @FXML public Button btnCompileFuzzyInputs ;
    
    @FXML public Tab tab_results;
    @FXML public Button btnCheckResults;
    @FXML public AnchorPane area_results ;
    
    @FXML private RadioButton selectBayes;
    @FXML private ToggleGroup selectResultRadioGroup;
    @FXML private RadioButton selectFuzzyBayes;    
    
    @FXML public Tab tab_empty ;    
    @FXML public Tab tab_notify;
    
//    HypNode hypNode ;                        // Representa os nodos de Hipótese criado    
//    ArrayList<EvidNode> evidNodes ;          // Representa os nodos de evidencia criados
    
    private InputHandler reader;                       // Representa uma instância do manipulador de entradas (que realiza os cálculos)
    private String fuzzyInputs;
    
    // Determina se a rede atualmente modelada já foi "compilada"
    // Compilação, neste caso, significa que o botão "compilar rede" foi ativado,
    // e que após sua ativação, todas as entradas fornecidas pelo usuário estavam corretas
    // de modo a permitir que os cálculos sejam realizados de forma satisfatória.
    // A verificação da corretude dos dados de entrada é feita após o botão de compilação ser acionado
//    private boolean compiledBayesianNet = false ; 
    
    //Constrói o controlador, o associando ao arquivo .fxml correspondente
    public MainScreen (Stage aStage)
    {
        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("/userInterface/MainScreen.fxml") );
        fxmlLoader.setRoot(this); 
        fxmlLoader.setController(this);
        this.stage = aStage;
        try
        {
            fxmlLoader.load();
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
        this.setUpTabStyles();
    }

    @FXML
    public void initialize()
    {
        
    }

    public InputHandler getReader() {
        return reader;
    }

    public void setReader(InputHandler reader) {
        this.reader = reader;
    }

    public String getFuzzyInputs()
    {
        return fuzzyInputs;
    }

    public void setFuzzyInputs(String fuzzyInputs)
    {
        this.fuzzyInputs = fuzzyInputs;
    }
    
    
    
    
    // Retorna o nodo de hipótese utilizado no contexto atual da aplicação (se houver algum)
    public HypNode getHypNode() 
    {
        HypNode hyp ;
        
        for ( Node child : this.getAllNodes(this))
        {
            if (child instanceof HypNode)
            {
                hyp = (HypNode) child;
                return hyp;
            }
        }        
        return null;        
    }

//    public void setHypNode(HypNode nodeHyp) 
//    {
//        this.hypNode = nodeHyp;
//    }

//    public boolean isCompiledBayesianNet() 
//    {
//        return compiledBayesianNet;
//    }
//
//    public void setCompiledBayesianNet(boolean compile) 
//    {
//        this.compiledBayesianNet = compile;
//    }
    
    
    
    // Retorna todas as evidências existentes no contexto atual da aplicação
    public ArrayList<EvidNode> getAllEvidNodes() 
    {
        ArrayList<EvidNode> evidenceNodes = new ArrayList<>();
        
        for ( Node child : this.getAllNodes(this))
        {
            if (child instanceof EvidNode)
            {
                evidenceNodes.add( (EvidNode) child);
            }
        }
        return evidenceNodes;
    }

    // Conta a quantidade de evidências existentes no contexto atual da aplicação
    public int getNumEvids() 
    {
        return this.getAllEvidNodes().size();
    }

//    public void setEvidNodes(ArrayList<EvidNode> nodesEvid)
//    {
//        this.evidNodes = nodesEvid;
//    }
    
//    public void addEvidNode (EvidNode evid)
//    {
//        this.evidNodes.add(evid);
//    }    
 
    // retorna todos os nodos descendentes de um Nodo
    public ArrayList<Node> getAllNodes(Parent root) 
    {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    // método complementar do "getAllNodes"
    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes)
    {
        for (Node node : parent.getChildrenUnmodifiable() )
        {
            nodes.add(node);
            if (node instanceof Parent)
            {
                addAllDescendents((Parent)node, nodes);
            }                
        }
    }
    
    // Remove todas as evidências presentes
    public void removeAllEvidences ()
    {
        for (Node child : this.getAllNodes(this))
        {
            if ( child instanceof EvidNode )
            {
                AnchorPane parent  = (AnchorPane) child.getParent();        
                parent.getChildren().remove(child);
            }
        }
    }
    
    
    // Funciona, mas não é utilizado...
    // Por padrão, a remoção de nodos de hipótese/evidência é feita de dentro de suas próprias classes
    public void removeEvidNode (EvidNode evid)
    {
        for (Node child : this.getAllNodes(this))
        {
            if ( child.equals(evid))
            {
                AnchorPane parent  = (AnchorPane) child.getParent();        
                parent.getChildren().remove(evid);
            }
        }
    }
    
    // Funciona, mas não é utilizado...
    // Por padrão, a remoção de nodos de hipótese/evidência é feita de dentro de suas próprias classes
    public void removehypNode (HypNode hyp)
    {
        for (Node child : this.getAllNodes(this))
        {
            if ( child.equals(hyp))
            {
                AnchorPane parent  = (AnchorPane) child.getParent();        
                parent.getChildren().remove(hyp);
            }
        }
    }

    
///////////////////////////////////////////////////////////////////////////////////////////////////    
///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// Funções dos itens dos menus ///////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
    
    @FXML
    public void exit() //MenuFile_Close
    {
        Platform.exit();
    }


///////////////////////////////////////////////////////////////////////////////////////////////////    
///////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////// Funções dos botões da toolbar //////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
    
    // Carrega só um arquivo de entrada Bayes
    @FXML
    public void chooseFileBayes(ActionEvent arg0) // btnBayes
    {
        this.reader = new InputHandler();
        
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        
        // Seleciona os arquivos sem a janela (facilitar testes)
//        File file = new File ("entradaBayes.txt");
//        String name = file.getName();
//        System.out.println("File nome : " + name);
//        String absolutePath = file.getAbsolutePath();
//        System.out.println("File absolute path : " + absolutePath);
        ///////////////////////////////////////////////////////////
        
        try
        {
            // Seleciona os arquivos com a janela     
            File file = fileChooser.showOpenDialog(this.stage);
            this.clearStyleErrorTab();
            String nome = file.getName();
            System.out.println("File nome : " + nome);
            String absolutePath = file.getAbsolutePath();
            System.out.println("File absolute path : " + absolutePath);
            ///////////////////////////////////////////////////////////

            //this.reader.loadTextFile(nome);
            this.reader.loadTextFile(absolutePath);

            this.reader.readBayes();
            this.reader.calculateBayesMultiHypStates();
//            this.clearStyleErrorTab();
        }
        catch (Exception e)
        {
            this.notifyError("> ERRO ao carregar arquivo: Nenhum arquivo selecionado - " + e.getMessage());
        }
//        this.reader.calculateBayesMultiEvid();
        //double bayesPuro = this.reader.calculateBayesMultiHypStates();
        //System.out.println(bayesPuro);
        
         
        //System.out.println("passou passou: " + absolutePath);
        
        // ativa a aba de resultados(?)
//        this.tab_results.setDisable(false);
        
    }

    // Carrega um arquivo de entrada bayes e um fuzzy
    @FXML
    public void chooseFileFuzzy(ActionEvent arg0) // btnFuzzy
    {
        this.reader = new InputHandler();
        
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        
        // Seleciona o arquivo BAYES sem a janela (facilitar testes)
//        File file = new File ("entradaBayes.txt");
//        String name = file.getName();
//        System.out.println("File nome : " + name);
//        String absolutePath = file.getAbsolutePath();
//        System.out.println("File absolute path : " + absolutePath);
        ///////////////////////////////////////////////////////////
        
        try
        {
            // Seleciona os arquivos bayes com a janela //////////////        
            File file = fileChooser.showOpenDialog(this.stage);
            this.clearStyleErrorTab();
            String name = file.getName();
            System.out.println("File nome : " + name);
            String absolutePath = file.getAbsolutePath();
            System.out.println("File absolute path : " + absolutePath);
            ///////////////////////////////////////////////////////////        

            this.reader.loadTextFile(absolutePath);        
            this.reader.readBayes();

            ///////////////////////////////////////////////////////////

            //teste fuzzy
            FileChooser fileChooser2 = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("FCL files (*.fcl)", "*.fcl");
            fileChooser2.getExtensionFilters().add(extFilter2);


            // Seleciona arquivo fcl com a janela
            //Show save file dialog
            File file2 = fileChooser2.showOpenDialog(this.stage);
            this.clearStyleErrorTab();

            ///////////////////////////////////////////////////

            // Seleciona os arquivos sem a janela (facilitar testes)
    //        File file2 = new File ("entradaFuzzy.fcl");
    //        String name2 = file2.getName();
    //        System.out.println("File nome : " + name2);
    //        String absolutePath2 = file2.getAbsolutePath();
    //        System.out.println("File absolute path : " + absolutePath2);
            ///////////////////////////////////////////////////////////

            String path = file2.getAbsolutePath();
            this.reader.readFuzzyFile(path);
            this.reader.calcFuzzyBayes(); 
        }
        catch (Exception e)
        {
            this.notifyError("> ERRO ao carregar arquivo: Nenhum arquivo selecionado - " + e.getMessage());
        }                
    }
    
    //Cria um Nodo de Hipótese
    @FXML
    public void createHypNodeUI () // btnCreateHypNode
    {
        HypNode hypNode = new HypNode(); // cria o objeto referente ao nodo de Hipótese
        MouseControlUtil.makeDraggable(hypNode); // faz com que o nodo criado seja um objeto "arrastável" na interface        
        hypNode.setMainControl(this);    // associa esta MainScreen ao HypNode criado
        this.area_bayes.getChildren().add(hypNode);  // Adiciona o elemento visual do nodo de hipótese ao contêiner da interface gráfica
        this.btnCreateHypNode.setDisable(true); // Desabilita o botão, pois só se pode haver 1 nodo de hipótese na rede
//        System.out.println("Criou hypNode");
//        this.setHypNode(hypNode);
    }
    
    //Cria um Nodo de Evidência
    @FXML
    public void createEvidNodeUI () // btnCreateEvidNode
    {
        // Verifica se a opção de compilação da rede não está habilitada
        if ( !this.btnCompileNetwork.isDisable() )
        {
            // Se o botão de compilação da rede estiver habilitado, então deve-se desativá-lo
            this.btnCompileNetwork.setDisable(true);
        }
        
        EvidNode evidNode = new EvidNode();       // cria o objeto referente ao nodo de Evidência
        MouseControlUtil.makeDraggable(evidNode); // faz com que o nodo criado seja um objeto "móvel" na interface        
        evidNode.setMainControl(this);            // associa esta MainScreen ao EvidNode criado
        
        // Se já houver uma hipótese, prepara os títulos das colunas do nodo de evidência
        if ( this.getHypNode() != null )
        {
            evidNode.colTitlesSetup();
        }
        
        // Adiciona o novo nodo de evidência ao contexto atual da aplicação
        this.area_bayes.getChildren().add(evidNode);        
//        this.addEvidNode(evidNode); //adiciona o nodo de evidência ao conjunto de evidências conhecidas
        
//        this.btnCreateHypNode.setDisable(true); // Desabilita o botão, pois só se pode haver 1 nodo de hipótese na rede
//        System.out.println("Criou EvidNode");
        
        // desabilita a edição do nodo de hipótese enquanto houver nodos de evidência
        if ( this.getHypNode().isLocked() )
        {
           this.getHypNode().btn_lockHypNode.setDisable(true);
        }
    }
    
///////////////////////////////////////////////////////////////////////////////////////////////////    
///////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////// Funções e métodos auxiliares ///////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
    
    // Monta a barra de notificação com a mensagem de erro adequada e adiciona um destaque visual
    public void notifyError (String erro)
    {
        this.tab_notify.setText("  " + erro + "  ");
        this.setStyleErrorTab();
    }
    
    // Monta o estilo de notificação de erro para a barra de notificações
    private void setStyleErrorTab ()
    {
        this.tab_notify.getStyleClass().clear();
        this.tab_notify.getStyleClass().add("tab-notify-errors");
    }
    
    // Limpa o estilo visual da barra de notificações e apaga quaisquer mensagens nela
    public void clearStyleErrorTab ()
    {
        this.tab_notify.setText(null);
        this.tab_notify.getStyleClass().clear();
        this.tab_notify.setStyle(null);
    }
 
    public void setUpTabStyles ()
    {
////        this.tabPane_areas.getStylesheets().clear();
////        this.tabPane_areas.getStyleClass().clear();
//        this.tabPane_areas.getStylesheets().add(getClass().getResource("/userInterface/styles.css").toExternalForm());
//        
//        this.tab_bayes.getStyleClass().clear();
//        this.tab_fuzzy.getStyleClass().clear();
//        this.tab_results.getStyleClass().clear();
        
//        this.tabPane_areas.getStylesheets().clear();
        
        this.tab_empty.getStyleClass().clear();
        
        this.tab_notify.getStyleClass().clear();        
        this.tab_bayes.getStyleClass().add("tab-style") ;
        this.tab_fuzzy.getStyleClass().add("tab-style") ;
        this.tab_results.getStyleClass().add("tab-style") ;
    }
    
    // Informa se há evidências no contexto atual da aplicação
    public boolean hasEvidences()
    {
        if ( this.getAllEvidNodes().size() < 1)
        {
            return false;   // Retorna "false" se não houver evidência alguma
        }
        return true ;       // Retorna "true" se não houver 1 evidência ou mais
    }
    
    // Retorna "true" apenas se TODAS as evidências estiverem corretamente preenchidas
    public boolean isWithAllEvidsComplete()
    {
        // Percorre todos os nodos de evidência
        for (EvidNode evid : this.getAllEvidNodes())
        {
            // Verifica se o nodo está correto
            boolean isFine = evid.isEvidNodeComplete() ;
            
            if ( !isFine ) // Se o noto estiver com problemas, retorna aqui
            {
                return false ;
            }
            
            // Caso o nodo esteja correto, garante que ele fique trancado
            evid.lockSpecificNode();
        }
        return true ;
    }
   
    // Retorna "true" apenas se o nodo de hipótese estiver corretamente preenchido
    public boolean isWithHypComplete()
    {
        // Verifica se o nodo está correto
        boolean isFine = this.getHypNode().isHypNodeComplete();
        return true ;
    }
    
    
    // Retorna "true" apenas se TODAS as evidências fuzzy estiverem corretamente preenchidas
    public boolean isWithAllFuzzyNodesComplete()
    {
        // Percorre todos os nodos de evidência
        for (FuzzyNode fuzzy : this.getAllFuzzyNodes())
        {
            // Verifica se o nodo está correto
            boolean isFine = fuzzy.isFuzzyNodeComplete();
            
            if ( !isFine ) // Se o noto estiver com problemas, retorna aqui
            {
                return false ;
            }
            
            // Caso o nodo esteja correto, garante que ele fique trancado
            fuzzy.lockSpecificNode();
        }
        return true ;
    }
    
    @FXML // Método executado ao se apertar o botão "btnCompileNetwork"
    public void compileBayesianNet()
    {
        // Desabilita a alteração dos nodos da rede bayesiana
        this.lockBayesianNetworkEdition();
        
        // Requisita aos nodos da rede que "assimilem" os inputs contidos neles
        this.computeBayesianInputs();       
        
        // Garant limpeza da área de inputs fuzzy
        this.clearFuzzyArea();
        
        // Carrega as informações para a área de input fuzzy
        this.createFuzzyNodes();
        
        // Habilita a inclusão de inputs fuzzy para o cálculo fuzzy-bayesiano
        this.tab_fuzzy.setDisable(false);

        // Garante a limpeza da área de resultados antes de mostrar
        this.area_results.getChildren().clear();
        
        // Reseta o estado dos botões da area_results
        this.selectResultRadioGroup.selectToggle(null);
        
        // Habilita a consulta de resultados sobre a rede inserida no programa
        this.tab_results.setDisable(false);
        
        // Habilita a opção para voltar a editar a rede bayesiana
        this.btnAllowEditBayes.setVisible(true);
        
        // Desabilita a compilação (até que uma edição seja feita ou até que se confirme que nada foi alterado)
        this.btnCompileNetwork.setDisable(true);
    }        
    
    public void lockBayesianNetworkEdition()
    {
        this.lockBayesianEvidsEdition();
        this.lockBayesianNodesExclusion();
    }
    
    public void lockBayesianEvidsEdition()
    {
        // Verifica se o botão de criação de evidências está ativado
        boolean disabled = this.btnCreateEvidNode.isDisable();
        
        // Se o botão estivar ativado, o desativa
        if ( !disabled )
        {
            this.btnCreateEvidNode.setDisable(true);
        }
        
        for ( EvidNode evid : this.getAllEvidNodes())
        {
            evid.lockEvidEdition();
        }
    }
    
    public void lockBayesianNodesExclusion()
    {
        // Verifica se o botão de criação de hypótese está ativado
        boolean disabled = this.btnCreateHypNode.isDisable();
        
        // Se o botão estivar ativado, o desativa
        if ( !disabled )
        {
            this.btnCreateHypNode.setDisable(true);
        }        
        
        this.getHypNode().lockHypExclusion();
        
        for ( EvidNode evid : this.getAllEvidNodes())
        {
            evid.lockEvidExclusion();
        }
    }
    
    // Extrai todas as informações fornecidas pelo usuário via interface gráfica
    public void computeBayesianInputs()
    {
        this.getHypNode().computeHypInputs();
        
//        ArrayList<Evidence> bayesEvidences = new ArrayList<Evidence>();
        
        for ( EvidNode evid : this.getAllEvidNodes())
        {
            evid.computeEvidInputs();
            
        }
        
        String hypInfo = this.getHypNode().hypothesis.showStates() ;
        hypInfo += this.getHypNode().hypothesis.showEvidences() ;
        System.out.println ("\n >>>>>>>>>>>> REDE COMPUTADA: >>>>>>>>>>>>>>>>>" + hypInfo);        
    }
    
    
    @FXML
    public void enableBayesianNetworkEdition()
    {
        //Desabilita especificamente a busca por resultados fuzzy
        if (!this.selectFuzzyBayes.isDisabled())
        {
            this.selectFuzzyBayes.setDisable(true);
        }
        
        // Desabilita a consulta de resultados sobre a rede inserida no programa
        this.tab_results.setDisable(true);
        
        // Desabilita a inclusão de inputs fuzzy para o cálculo fuzzy-bayesiano
        this.tab_fuzzy.setDisable(true);
        
        // Desabilita a opção para voltar a editar a rede bayesiana
        this.btnAllowEditBayes.setVisible(false);
        
        // Habilita a compilação (até que uma edição seja feita ou até que se confirme que nada foi alterado)
        this.btnCompileNetwork.setDisable(false);
        
        // Reabilita a alteração dos nodos da rede bayesiana
        this.unlockBayesianNetworkEdition();
    }
    
    public void unlockBayesianNetworkEdition()
    {
        // Verifica se o botão de criação de evidências está desativado
        boolean disabled = this.btnCreateEvidNode.isDisable();
        
        // Se o botão estivar desativado, o ativa
        if ( disabled )
        {
            this.btnCreateEvidNode.setDisable(false);
        }
        
        this.unlockBayesianEvidsEdition();
        this.unlockBayesianNodesExclusion();
    }
    
    public void unlockBayesianEvidsEdition()
    {
        for ( EvidNode evid : this.getAllEvidNodes())
        {
            evid.unlockEvidEdition();
        }
    }
    
    public void unlockBayesianNodesExclusion()
    {
        this.getHypNode().unlockHypExclusion();
        
        for ( EvidNode evid : this.getAllEvidNodes())
        {
            evid.unlockEvidExclusion();
        }
    }

    // Retorna todas as evidências existentes no contexto atual da aplicação
    public ArrayList<FuzzyNode> getAllFuzzyNodes() 
    {
        ArrayList<FuzzyNode> fuzzyNode = new ArrayList<FuzzyNode>();
        
        for ( Node child : this.getAllNodes(this))
        {
            if (child instanceof FuzzyNode)
            {
                fuzzyNode.add( (FuzzyNode) child);
            }
        }
        return fuzzyNode;
    }

    private void createFuzzyNodes()
    {
        for ( EvidNode child : this.getAllEvidNodes() )
        {
            FuzzyNode newFuzzyNode = new FuzzyNode ();
            newFuzzyNode.setEvidence(child.evidence) ;
            newFuzzyNode.fuzzyNodeSetup();
            MouseControlUtil.makeDraggable(newFuzzyNode);
            newFuzzyNode.setMainControl(this);
            this.area_fuzzy.getChildren().add(newFuzzyNode) ;
//            this.area_results.getChildren().add(newFuzzyNode) ;
        }
    }
    
    @FXML 
    private void computeFuzzyInputs()
    {        
//       ArrayList<FuzzyNode> fuzzyNode = new ArrayList<FuzzyNode>();
        
        for ( Node child : this.getAllNodes(this))
        {
            if (child instanceof FuzzyNode)
            {
                FuzzyNode child2 = (FuzzyNode) child ;
                
                child2.computeFuzzyInputs();
            }
        }  
        FuzzyFileFormatter fclCreator = new FuzzyFileFormatter();
        fclCreator.setMainScreen(this);
        
        this.fuzzyInputs = fclCreator.getTextFCL() ;
        
        // Só deve permitir visualização de resultados que incluem fuzzy após compilação dos inputs fuzzy 
        this.selectFuzzyBayes.setDisable(false);
        
        // TESTE ~ computeFuzzyInputs()
        System.out.println ("\n\n ~~~~~~~TEXTO .fcl GERADO A PARTIR DO INPUT: " + this.fuzzyInputs);
    }
    
    public ArrayList<FuzzyEvid> getFuzzyEvids ()
    {
        ArrayList<FuzzyEvid> fuzzyEvids = new ArrayList<FuzzyEvid>();
        
        for ( Node child : this.getAllNodes(this))
        {
            if (child instanceof FuzzyNode)
            {
                FuzzyNode child2 = (FuzzyNode) child ;
                fuzzyEvids.add(child2.getFuzzyEvid()) ;
                child2.computeFuzzyInputs();
            }
        }        
        return fuzzyEvids;
    }
    
    // Remove todos os componentes gráficos da área de inputs fuzzy
    public void clearFuzzyArea()
    {
        ObservableList<Node> children = this.area_fuzzy.getChildren();
        this.area_fuzzy.getChildren().removeAll(children);
    }
    
    @FXML   // Prepara a consulta de um resultado com bayes puro
    void setupBayesianResult(ActionEvent event)
    {
        // Garante a limpeza da área de resultados antes de mostrar
        this.area_results.getChildren().clear();
        
//        // Verifica se a opção calcular resultados está desabilitada
//        if ( this.btnCheckResults.isDisable() )
//        {
//            // Se se a opção calcular resultados estiver desabilitada, ela deve ser ativada
//            this.btnCheckResults.setDisable(false);
//        }
        
        // cria o objeto referente à consulta de resultados bayesianos para a rede atual
        SetupBayesResult bayesResult = new SetupBayesResult();
        
        // Fornece ao objeto as informações referentes à rede atual
        bayesResult.setResultBayesHyp(this.getHypNode().getHypothesis());
        
        // Cria as opções para consulta de resultados
        bayesResult.setupResultChoices();
        
//        MouseControlUtil.makeDraggable(evidNode); // faz com que o nodo criado seja um objeto "móvel" na interface        
        bayesResult.setMainControl(this);            // associa esta MainScreen ao nodo criado
        
        // Adiciona o novo nodo ao contexto atual da aplicação
        this.area_results.getChildren().add(bayesResult);
        
        
        
//        System.out.println("Criou EvidNode");
        
//        // desabilita a edição do nodo de hipótese enquanto houver nodos de evidência
//        if ( this.getHypNode().isLocked() )
//        {
//           this.getHypNode().btn_lockHypNode.setDisable(true);
//        }
        
    }
    
    @FXML
    public void computeResultsFromGUI()
    {
        // Garante a limpeza da area de resultados antes de calcular        
        if (this.selectBayes.isSelected())
        {
            this.accessBayesResult().showResult();
        }
        else if (this.selectFuzzyBayes.isSelected())
        {
            this.accessFuzzyBayesResult().showResult();
        }
    }
    
    public SetupBayesResult accessBayesResult()
    {
        for ( Node child : this.area_results.getChildren() )
        {
            if ( child instanceof SetupBayesResult)
            {
                return (SetupBayesResult) child ;
            }
        }
        return null ;
    }
    
    public SetupFuzzyBayesResult accessFuzzyBayesResult()
    {
        for ( Node child : this.area_results.getChildren() )
        {
            if ( child instanceof SetupFuzzyBayesResult)
            {
                return (SetupFuzzyBayesResult) child ;
            }
        }
        return null ;
    }
    
    
    
    @FXML   // Prepara a consulta de um resultado fuzzy-bayesiano
    void setupFuzzyBayesianResult(ActionEvent event)
    {
         // Garante a limpeza da área de resultados antes de mostrar
        this.area_results.getChildren().clear();
        
//        // Verifica se a opção calcular resultados está desabilitada
//        if ( this.btnCheckResults.isDisable() )
//        {
//            // Se se a opção calcular resultados estiver desabilitada, ela deve ser ativada
//            this.btnCheckResults.setDisable(false);
//        }
        
        // cria o objeto referente à consulta de resultados fuzzy-bayesianos para a rede atual
        SetupFuzzyBayesResult fuzzyBayesResult = new SetupFuzzyBayesResult();
        
        // Fornece ao objeto as informações referentes à rede atual
        fuzzyBayesResult.setResultBayesHyp(this.getHypNode().getHypothesis());
        
        // Cria as opções para consulta de resultados
        fuzzyBayesResult.setupResultChoices();
        
//        MouseControlUtil.makeDraggable(evidNode); // faz com que o nodo criado seja um objeto "móvel" na interface        
        fuzzyBayesResult.setMainScreen(this);            // associa esta MainScreen ao nodo criado
        
        // Adiciona o novo nodo ao contexto atual da aplicação
        this.area_results.getChildren().add(fuzzyBayesResult);
    }
    
    @FXML
    private void blockComputeResults()
    {
        this.btnCheckResults.setDisable(true);
    }
    
    
    
}
