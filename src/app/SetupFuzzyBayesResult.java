package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class SetupFuzzyBayesResult extends AnchorPane
{
    private Hypothesis resultBayesHyp ;
    
    private ArrayList<Evidence> resultBayesEvids = new ArrayList<>();
    private ArrayList<Evidence> evidsWithFuzzyMeasures = new ArrayList<>();
    private ArrayList<Evidence> chosenEvidsWithStates = new ArrayList<>();
    
    private String resultOutput ;
    
    private final String EVID_STATE_RADIO_BTN = "bayes-evidState-radio-btn" ;
    private final String EVID_NAME_CHECK_BOX = "bayes-evid-check-box" ;
    
    private MainScreen mainScreen ;
    private final SetupFuzzyBayesResult self;
    
    private boolean locked ;        // armazena o estado atual do nodo quanto à sua editabilidade (true = não editável, false = editável)  
    
    @FXML    private AnchorPane root_fuzzyBayes_result;
    @FXML    private HBox hbox_main_fuzzyBayes_result;
    
    @FXML    private Accordion accordion_fuzzyBayes_result;
    
    @FXML    private TitledPane evids_fuzzyBayes_chooser_display;
    @FXML    private AnchorPane evids_fuzzyBayes_chooser_areaPane;
    @FXML    private VBox evids_fuzzyBayes_chooser_area;
    
    @FXML    private TitledPane measures_chooser_display;
    @FXML    private AnchorPane measures_chooser_areaPane;
    @FXML    private VBox measures_area;
    
    @FXML    private ScrollPane scroll_pane_fuzzyBayes_result_area;
    @FXML    public Text fuzzyBayes_result_output_text;
    
   

    
    
    //Cria o controlador do nodo de hipótese e o associa ao .fxml correspondente
    public SetupFuzzyBayesResult() 
    {
        FXMLLoader fxmlLoader = new FXMLLoader( getClass().getResource("/userInterface/SetupFuzzyBayesResult.fxml") );
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

    public void setMainScreen (MainScreen control)
    {
        this.mainScreen = control ;
    }

    public ArrayList<Evidence> getEvidsWithFuzzyMeasures() {
        return evidsWithFuzzyMeasures;
    }

    public void setEvidsWithFuzzyMeasures(ArrayList<Evidence> evidsWithFuzzyMeasures) {
        this.evidsWithFuzzyMeasures = evidsWithFuzzyMeasures;
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
                        ArrayList<Evidence> chosenEvids = self.evidsWithFuzzyMeasures;
                        chosenEvids.add(evid);
                        self.setEvidsWithFuzzyMeasures(chosenEvids);
                        
                        // Caso seja a primeira evidência selecionada, habilita o input de medida
                        if (self.measures_chooser_display.isDisabled())
                        {
                            self.measures_chooser_display.setDisable(false);
                        }
                        
                    }
                    else if ( newValue == false)   // Se foi desmarcado, remove a evidência ao conjunto das escolhidas
                    {
                        ArrayList<Evidence> chosenEvids = self.evidsWithFuzzyMeasures;
                        chosenEvids.remove(evid);
                        self.setEvidsWithFuzzyMeasures(chosenEvids);                        
                        
                        // Caso não haja evidência selecionada
                        if (self.isEmptyEvidSelection())
                        {
                            self.measures_chooser_display.setDisable(true);
                            self.measures_chooser_display.setExpanded(false);
                        }
                    }                    
                }
            });
            
            // Adiciona a nova opção à devida àrea
            this.evids_fuzzyBayes_chooser_area.getChildren().add(evidBox);            
        }
    }
    
    // Retorna "true" se nenhuma evidência estiver selecionada
    public boolean isEmptyEvidSelection()
    {
        for (Node child : this.mainScreen.getAllNodes(this.evids_fuzzyBayes_chooser_area))
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
    
    @FXML
    private void blockComputeResults()
    {
        self.mainScreen.btnCheckResults.setDisable(true);
    }
    
    @FXML   // Gera as opções de evidências da hipótese para serem escolhidas
    private void addFuzzyMeasuresOptions()
    {
        this.blockComputeResults();
        
        // Garante a limpeza da área de resultados antes de mostrar
        this.measures_area.getChildren().clear();
        
        // Percorre todas as evidências escolhidas
        for (Evidence evid : this.evidsWithFuzzyMeasures)
        {
            HBox evidAndMeasure = new HBox();            
//            evidAndMeasure.getStyleClass().add("hbox-evid-measure");            
            
            // Cria uma nova opção para a evidência atual
            Label evidName = new Label(evid.getNodeName());
            
            //Adiciona estilo 
            evidName.getStyleClass().add("label-fuzzy-mesures");
            
            // Cria um campo para registrar a medida referente a essa evidência
            TextField measureField = new TextField();
            
            // Adiciona texto "placeholder" ao campo
            measureField.setPromptText("Medição");        
            
            // Adiciona estilo
            measureField.getStyleClass().add("fuzzy-measure-field");

            // Adiciona o filtro de formato numérico ao textField de probabilidades
            measureField.setTextFormatter(this.formatNumericTextField());
            
            // Registra a evidência à qual essa medição pertence
            measureField.setId(evid.getNodeName());
            
//            evidStateProbTxtField.setPadding(Insets.EMPTY);
            measureField.setMinWidth(70);
            
            // Adicionar listener para verificar edição de um campo
            measureField.textProperty().addListener
            (
                (observable, oldValue, newValue) ->
                {
                     if (!newValue.isEmpty())
                    {
                        if ( self.isWithAllSetMeasures() && self.mainScreen.btnCheckResults.isDisabled())
                        {
                            self.mainScreen.btnCheckResults.setDisable(false);
                        }                        
                        // TESTE - adicionando medição para evidência
                        //                    System.out.println("textfield changed from " + oldValue + " to " + newValue);
                        String measure = measureField.getText(); // acessa o nome do estado escolhido
                        System.out.println("TESTE - Evidencia \"" + measureField.getId() + "\" ~ Medição inserida: " + measure); 
                    }
                    else
                    {
                        self.mainScreen.btnCheckResults.setDisable(true);
                    }
                }
            );            
            // Insere o nome da evidência e o campo de medição num conteiner horizontal (HBox)
            evidAndMeasure.getChildren().add(evidName);
            evidAndMeasure.getChildren().add(measureField);            

            // Adiciona o conteiner montado ao local apropriado para que seja exibido
            this.measures_area.getChildren().add(evidAndMeasure);            
        }
    }
    
    // Verifica se todas as evidencias tiveram medições adicionadas
    public boolean isWithAllSetMeasures()
    {
         for ( Node child : this.mainScreen.getAllNodes(this.measures_area) )
        {
            if ( child instanceof TextField)
            {
               TextField measureField = (TextField) child ;
               if ( measureField.getText().isEmpty() )
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
    
    // Obtém as mediçoes fornecidas pelo usuário para realizar o cálculo fuzzy-bayesiano
    public void computeResult()
    {
        //prepara as evidencias escolhidas para que cada uma contenha apenas o estado desejado
        ArrayList<Evidence> evidsWithMeasures = new ArrayList<>();
        ArrayList<TextField> visitedMeasures = new ArrayList<>();
//        TextField visitedMeasure ;
        for ( Node child : this.mainScreen.getAllNodes(this.measures_area))
        {
            if ( child instanceof TextField)
            {                
                TextField evidMeasure = (TextField) child;
                
                if (visitedMeasures.isEmpty())
                {
                    visitedMeasures.add(evidMeasure);
                    
                    float measureValue = -1;

                    try
                    {
                        measureValue = Float.parseFloat(evidMeasure.getText());
                    }
                    catch (Exception e)
                    {
                        System.out.println("%% ERRO %% Medida INVÁLIDA encontrada: " + evidMeasure.getText());
                    }
                    
                    Evidence completeEvid = this.getEvidByName(evidMeasure.getId());
                    completeEvid.setMeasure(measureValue);
                    evidsWithMeasures.add(completeEvid);
                    
                    
                    // TESTE                    
                    System.out.println("%% FEITO! %% Medida \"" + evidsWithMeasures.get(0).getMeasure() + 
                            "\" para a PRIMEIRA evidência: " + evidsWithMeasures.get(0).getNodeName());
                }
                else if (!visitedMeasures.contains(evidMeasure))
                {
                    visitedMeasures.add(evidMeasure);

                    float measureValue = -1;

                    try
                    {
                        measureValue = Float.parseFloat(evidMeasure.getText());
                    }
                    catch (Exception e)
                    {
                        System.out.println("%% ERRO %% Medida INVÁLIDA encontrada: " + evidMeasure.getText());
                    }

                    Evidence completeEvid = this.getEvidByName(evidMeasure.getId());
                    completeEvid.setMeasure(measureValue);
                    evidsWithMeasures.add(completeEvid);
                    
                    // TESTE
                    int lastEvid = evidsWithMeasures.size()-1;
                    System.out.println("%% FEITO! %% Medida \"" + evidsWithMeasures.get(lastEvid).getMeasure() + 
                        "\" para a " + lastEvid + "-esima evidência: " + evidsWithMeasures.get(lastEvid).getNodeName());                         
                }
            }
            this.chosenEvidsWithStates = evidsWithMeasures ;        
        }
    }
    
    // Exibe o resultado da consulta fuzzy-bayesiana na "area_results"
    public void showResult()
    {
        this.computeResult();
        
        InputHandler reader = new InputHandler();
        
        // Pega o texto em formato .fcl gerado pela compilação dos inputs fuzzy
        String fuzzyInputs = this.mainScreen.getFuzzyInputs();
        
        //inicializa o reader com os inputs fuzzy
        reader.readFuzzyFromString(fuzzyInputs);
        
        String output = reader.calcFuzzyBayesUI(this.resultBayesHyp, this.chosenEvidsWithStates);
        this.fuzzyBayes_result_output_text.setText(output);        
        this.mainScreen.btnCheckResults.setDisable(true);
        
        //TESTE
        System.out.println("\t#### RESULTADO OBTIDO! CALCULO FUZZY-BAYESIANO VIA GUI CONCLUIDO: ####### \n" + output);   
    }
    
    // Encontra uma evidência a partir de seu nome
    public Evidence getEvidByName(String evidName)
    {
        Evidence evid = new Evidence();
        
        for ( Evidence theEvid : this.evidsWithFuzzyMeasures)
        {
            if ( theEvid.getNodeName().equals(evidName))
            {
                return theEvid ;
            }
        }        
        return evid;
    }

//    private EvidenceState getEvidStateByNameAndEvid(String stateName, Evidence completeEvid)
//    {
//        EvidenceState state = completeEvid.getStateByName(stateName);
//        return state ;        
//    }
    
    
    // Cria um formatador de texto para garantir apenas inputs numéricos nos textFields de medições
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
        
}

// método que tentava adicionar as evidencias escolhidas num array temporario
// foi substituido por um metodo que exclui as não escolhidas
//public void computeResult()
//    {
//        //prepara as evidencias escolhidas para que cada uma contenha apenas o estado desejado
//        ArrayList<Evidence> evidsWithStatesArray = new ArrayList<>();
//        for ( Node child : this.mainScreen.getAllNodes(this.evidStates_chooser_area))
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