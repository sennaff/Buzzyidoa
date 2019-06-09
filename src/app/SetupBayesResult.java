package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class SetupBayesResult extends AnchorPane
{
    private Hypothesis resultBayesHyp ;
    
    private ArrayList<Evidence> resultBayesEvids = new ArrayList<>();
    private ArrayList<Evidence> chosenBayesEvids = new ArrayList<>();
    private ArrayList<Evidence> chosenEvidsWithStates = new ArrayList<>();
    
    private String resultOutput ;
    
    private final String EVID_STATE_RADIO_BTN = "bayes-evidState-radio-btn" ;
    private final String EVID_NAME_CHECK_BOX = "bayes-evid-check-box" ;
    
    private MainScreen mainControl ;
    private final SetupBayesResult self;
    
    private boolean locked ;        // armazena o estado atual do nodo quanto à sua editabilidade (true = não editável, false = editável)  
  
    @FXML    private AnchorPane root_bayes_result;
    @FXML    private HBox hbox_main_bayes_result;
    
    @FXML    private Accordion accordion_bayes_result;
    
    @FXML    private TitledPane evids_chooser_display;
    @FXML    private AnchorPane evids_chooser_areaPane;
    @FXML    private VBox evids_chooser_area;
    
    @FXML    private TitledPane evidStates_chooser_display;
    @FXML    private AnchorPane evidStates_chooser_areaPane;
    @FXML    private VBox evidStates_chooser_area;
    
    @FXML    private ScrollPane scroll_pane_bayes_result_area;
    @FXML    public Text bayes_result_output_text;
    
    
    //Cria o controlador do nodo de hipótese e o associa ao .fxml correspondente
    public SetupBayesResult() 
    {
        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("/userInterface/SetupBayesResult.fxml") );
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

    public void setMainControl (MainScreen control)
    {
        this.mainControl = control ;
    }

    public ArrayList<Evidence> getChosenBayesEvids() {
        return chosenBayesEvids;
    }

    public void setChosenBayesEvids(ArrayList<Evidence> chosenBayesEvids) {
        this.chosenBayesEvids = chosenBayesEvids;
    }
    
    
    
    public Hypothesis getResultBayesHyp() {
        return resultBayesHyp;
    }

    public void setResultBayesHyp(Hypothesis resultBayesHyp) {
        this.resultBayesHyp = resultBayesHyp;
    }

    public ArrayList<Evidence> getResultBayesEvids() {
        return resultBayesEvids;
    }

    public void setResultBayesEvids(ArrayList<Evidence> resultBayesEvids) {
        this.resultBayesEvids = resultBayesEvids;
    }

    public String getResultOutput() {
        return resultOutput;
    }

    public void setResultOutput(String resultOutput) {
        this.resultOutput = resultOutput;
    }

    // monta as opções de consulta de resultados
    void setupResultChoices()
    {
        if (this.getResultBayesHyp() != null)
        {
            this.addHypEvidOptions();
        }
    }

    // Gera as opções de evidências da hipótese para serem escolhidas
    private void addHypEvidOptions()
    {
        // Percorre todas as evidências
        for (Evidence evid : this.resultBayesHyp.getBayesEvids())
        {
            // Cria um novo campo de opção para a evidência atual
            CheckBox evidBox = new CheckBox(evid.getNodeName());
            
            // Confere um identificador de tipo ao campo de opção
            evidBox.setId(EVID_NAME_CHECK_BOX);
            
//            evidBox.getStyleClass().add("check-box-option"); // Adiciona o estilo
            
            // Adiciona um listener para verificar se o campo foi marcado ou desmarcado
            evidBox.selectedProperty().addListener(new ChangeListener<Boolean>()
            {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
                {
                    if (newValue == true) // Se foi marcado, adiciona a evidência ao conjunto das escolhidas
                    {
                        ArrayList<Evidence> chosenEvids = self.chosenBayesEvids;
                        chosenEvids.add(evid);
                        self.setChosenBayesEvids(chosenEvids);
                        
                        // Caso seja a primeira evidência adicionada, habilita o seletor de estados
                        if (self.evidStates_chooser_display.isDisabled())
                        {
                            self.evidStates_chooser_display.setDisable(false);
                        }
                        
                    }
                    else if ( newValue == false)   // Se foi desmarcado, remove a evidência ao conjunto das escolhidas
                    {
                        ArrayList<Evidence> chosenEvids = self.chosenBayesEvids;
                        chosenEvids.remove(evid);
                        self.setChosenBayesEvids(chosenEvids);                        
                        
                        // Caso não haja evidência selecionada
                        if (self.isEmptyEvidSelection())
                        {
                            self.evidStates_chooser_display.setDisable(true);
                            self.evidStates_chooser_display.setExpanded(false);
                        }
                    }                    
                }
            });
            
            // Adiciona a nova opção à devida àrea
            this.evids_chooser_area.getChildren().add(evidBox);            
        }
    }
    
    // Retorna "true" se nenhuma evidência estiver selecionada
    public boolean isEmptyEvidSelection()
    {
        for (Node child : this.mainControl.getAllNodes(this.evids_chooser_area))
        {
            if ( child instanceof CheckBox)
            {
                // Cria um novo campo para verificar cada opção
                CheckBox evidBox = (CheckBox) child;
                if (evidBox.isSelected())
                {
                    return false ;
                }
            }
        }
        return true ;
    }
    
    @FXML   //impede que resultados sejam computados num momento indevido
    private void blockComputeResults()
    {
        self.mainControl.btnCheckResults.setDisable(true);
    }
    
    @FXML   // Gera as opções de evidências da hipótese para serem escolhidas
    private void addEvidStatesOptions()
    {
        this.blockComputeResults();
        
        // Garante a limpeza da área de resultados antes de mostrar
        this.evidStates_chooser_area.getChildren().clear();
        
        // Percorre todas as evidências escolhidas
        for (Evidence evid : this.chosenBayesEvids)
        {
            //Cria uma nova opção para a evidência atual
            Label evidName = new Label(evid.getNodeName());
            
            // Adiciona estilo
            evidName.getStyleClass().add("label-fuzzy-mesures");
            this.evidStates_chooser_area.getChildren().add(evidName);
            
            // create a toggle group 
            ToggleGroup tg = new ToggleGroup();
            
            tg.setUserData(evid.getNodeName());
            
            for (EvidenceState evidState : evid.getEvidenceStates())
            {
                //Cria uma nova opção para a evidência atual
                RadioButton radioBtn = new RadioButton(evidState.getNameEvidState());
                
                // Identifica a evidência à qual este estado pertence
                radioBtn.setId(evid.getNodeName());
                
                // add radiobuttons to toggle group 
                radioBtn.setToggleGroup(tg); 
                
                // Adiciona estilo
                radioBtn.getStyleClass().add("radio-button-state");
                this.evidStates_chooser_area.getChildren().add(radioBtn);
            }
            
            // Adiciona um listener ao grupo de botões referente aos estado da evidência atual
            tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>()  
            { 
                public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) 
                {
                    RadioButton rb = (RadioButton)tg.getSelectedToggle();

                    if (rb != null)
                    {
                        if ( self.isWithAllStatesSelected() && self.mainControl.btnCheckResults.isDisabled())
                        {
                            self.mainControl.btnCheckResults.setDisable(false);
                        }
                        
                        String s = rb.getText(); // acessa o nome do estado escolhido

                        // TESTE - adicionando estado de evidência                        
                        System.out.println("TESTE - Estado selecionado: " + s); 
                    } 
                } 
            }); 
        }
    }
    
    // Verifica se todas as evidencias tiveram ao menos 1 esctado escolhido
    public boolean isWithAllStatesSelected()
    {
         for ( Node child : this.evidStates_chooser_area.getChildren() )
        {
            if ( child instanceof RadioButton)
            {
               RadioButton rb = (RadioButton) child ;
               if ( (RadioButton)rb.getToggleGroup().getSelectedToggle() == null)
               {
                   return false ;
               }
            }
        }
        return true ;
    }
    
//    // Verifica se algum estado foi escolhido
//    public boolean isWithAnyStateSelected()
//    {
//         for ( Node child : this.evidStates_chooser_area.getChildren() )
//        {
//            if ( child instanceof RadioButton)
//            {
//               RadioButton rb = (RadioButton) child ;
//               if ( (RadioButton)rb.getToggleGroup().getSelectedToggle() != null)
//               {
//                   return true ;
//               }
//            }
//        }
//        return false ;
//    }
//    
//    
    
    public void computeResult()
    {
        //prepara as evidencias escolhidas para que cada uma contenha apenas o estado desejado
        ArrayList<Evidence> evidsWithStatesArray = new ArrayList<>();
        ArrayList<EvidenceState> visitedStates = new ArrayList<>();
        EvidenceState newState ;
        for ( Node child : this.mainControl.getAllNodes(this.evidStates_chooser_area))
        {
            if ( child instanceof RadioButton)
            {                
                RadioButton rBtn = (RadioButton) child;
                
                // TESTE
                String stateName = rBtn.getText();
                System.out.println("%%%% RadioButton encontrado: " + stateName);
                
                RadioButton selectedBtn = (RadioButton)rBtn.getToggleGroup().getSelectedToggle();
                if (selectedBtn.isSelected())
                {
//                    ArrayList<EvidenceState> chosenStates = new ArrayList<EvidenceState>();
                    
                    String stateNameChoice = selectedBtn.getText();
                    
                    //Teste
                    //System.out.println("%%%% RadioButton SELECIONADO: " + stateNameChoice);
                    
                    Evidence completeEvid = this.getEvidByName(selectedBtn.getId());
                    String evidName = completeEvid.getNodeName();
                    
                    EvidenceState selectedState = this.getEvidStateByNameAndEvid(stateNameChoice, completeEvid);
                    
                    if (visitedStates.contains(selectedState))
                    {
                        continue ;
                    }
                    
                    //Teste
                    System.out.println("%%%% Evid - " + completeEvid.getNodeName() + " - Estado selecionado: \"" + selectedState.getNameEvidState() +
                            "\" - Probs do estado: "+ selectedState.showProbs_Evid_given_Hyp()) ;
                    
                    if (visitedStates.isEmpty())
                    {
                        visitedStates.add(selectedState);
                    }
                    else if (!visitedStates.contains(selectedState))
                    {
                        visitedStates.add(selectedState);
                    }
                    
                    if (evidsWithStatesArray.isEmpty())
                    {
                        Evidence evid = new Evidence(evidName, selectedState);
                        evidsWithStatesArray.add(evid) ;
                        //Teste
                        System.out.println("%%%% ADD ~ FIRST Evid - " + completeEvid.getNodeName() + " - Estado selecionado: \"" + selectedState.getNameEvidState() +
                            "\" - Probs do estado: "+ selectedState.showProbs_Evid_given_Hyp()) ;
                    }
                    else //if (!visitedStates.contains(selectedState))
                    {
                        Evidence evid = new Evidence(evidName, selectedState);
                        evidsWithStatesArray.add(evid) ;
                        //Teste
                        System.out.println("%%%% ADD ~ NEXT Evid - " + completeEvid.getNodeName() + " - Estado selecionado: \"" + selectedState.getNameEvidState() +
                            "\" - Probs do estado: "+ selectedState.showProbs_Evid_given_Hyp()) ;
                    }                    
                }                
            }
        }
        this.chosenEvidsWithStates = evidsWithStatesArray ;        
    }
    
    
    public void showResult()
    {
        this.computeResult();
        
        InputHandler reader = new InputHandler();
        String output = reader.calculateBayesMultiHypStatesUI(this.resultBayesHyp, this.chosenEvidsWithStates);
        this.bayes_result_output_text.setText(output);
        
        this.mainControl.btnCheckResults.setDisable(true);

    }
    
    public Evidence getEvidByName(String evidName)
    {
        Evidence evid = new Evidence();
        
        for ( Evidence theEvid : this.chosenBayesEvids)
        {
            if ( theEvid.getNodeName().equals(evidName))
            {
                return theEvid ;
            }
        }        
        return evid;
    }

    private EvidenceState getEvidStateByNameAndEvid(String stateName, Evidence completeEvid)
    {
        EvidenceState state = completeEvid.getStateByName(stateName);
        return state ;        
    }
        
}

// método que tentava adicionar as evidencias escolhidas num array temporario
// foi substituido por um metodo que exclui as não escolhidas
//public void computeResult()
//    {
//        //prepara as evidencias escolhidas para que cada uma contenha apenas o estado desejado
//        ArrayList<Evidence> evidsWithStatesArray = new ArrayList<>();
//        for ( Node child : this.mainControl.getAllNodes(this.evidStates_chooser_area))
//        {
//            if ( child instanceof RadioButton)
//            {                
//                RadioButton rBtn = (RadioButton) child;
//                RadioButton selectedBtn = (RadioButton)rBtn.getToggleGroup().getSelectedToggle();
//                if (selectedBtn.isSelected())
//                {
//                    ArrayList<EvidenceState> chosenStates = new ArrayList<EvidenceState>();
//                    
//                    String stateName = selectedBtn.getText();
//                    Evidence completeEvid = this.getEvidByName(selectedBtn.getId());
//                    String evidName = completeEvid.getNodeName();
//                    
//                    chosenStates.add(this.getEvidStateByNameAndEvid(stateName, completeEvid));
//                    Evidence evid = new Evidence(evidName, chosenStates);
//                    
//                    evidsWithStatesArray.add(evid) ;
//                }                
//            }
//        }
//        
//    }
//    