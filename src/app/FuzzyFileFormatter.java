//
//FUNCTION_BLOCK variaveis_fuzzy_da_hipotese_x    // \n + $FUNCTION_BLOCK + hyp.getnome +
//                                                // \n +
//FUZZIFY barulho                                 // \n + $FUZZIFY + evid(i).name +
//	TERM baixo := trian 0 10 70;            // \n + $TERM + evid(i).state(j).name + $:= + #functionType + @v1 + @v2 + @v3 + $;    // #functionType = trian, n(v) = 3
//	TERM alto := (10,0) (70,1)(100,1);      // \n + $TERM + evid(i).state(j+1).name + $:= + #functionType + (@x1,y1) + (@x2,y2) + (@x3,y3) + $;    // #functionType = null (default), n(p) >=2 
//END_FUZZIFY                                     // \n + $END_FUZZIFY +
//                                                // \n +
//FUZZIFY iluminacao                              // \n + $FUZZIFY + evid(i+1).name +
//	TERM claro := trape 0 15 27 85;         // \n + $TERM + evid(i+1).state(j).name + $:= + #functionType + @v1 + @v2 + @v3 + @v4 + $;    // #functionType = trape, n(v) = 4
//	TERM escuro := (5,0) (90,1)(100,1);     // \n + $TERM + evid(i+1).state(j+1).name + $:= + #functionType + (@x1,y1) + (@x2,y2) + (@x3,y3) + $;    // #functionType = null (default), n(p) >=2 
//END_FUZZIFY                                     // \n + $END_FUZZIFY +
//                                                // \n +
//FUZZIFY espaco                                  // \n + $FUZZIFY + evid(i+n).name +
//	TERM apertado := (0, 1)  (80, 0) ;      // \n + $TERM + evid(i+n).state(j).name + $:= + #functionType + (@x1,y1) + (@x2,y2) + (@x3,y3) + $;    // #functionType = null (default), n(p) >=2 
//	TERM amplo := gauss 56 92;              // \n + $TERM + evid(i+n).state(j+1).name + $:= + @v1 + @v2 + $;    // #functionType = gauss, n(p) = 2
//END_FUZZIFY                                     // \n + $END_FUZZIFY +
//                                                // \n +
//END_FUNCTION_BLOCK                              // \n + $END_FUNCTION_BLOCK

package app;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Senna
 */
public class FuzzyFileFormatter
{    
    // Blocos textuais para a construção do texto lido pelo jFuzzy (para entrada de dados via interface gráfica)
    
    private final String FUNCTION_BLOCK = "FUNCTION_BLOCK" ;
    private final String SPACE = " " ;                              // Não há certeza se será usado
    private final String NEXT_LINE = "\n" ;
    private final String TAB = "\t" ;
    private final String VAR_INPUT = "VAR_INPUT" ;
    private final String COLON = ":" ;
    private final String REAL = "REAL" ;
    private final String SEMICOLON = ";" ;
    private final String END_VAR = "END_VAR" ;
    private final String FUZZIFY = "FUZZIFY" ;
    private final String TERM = "TERM" ;
    private final String EQUAL = "=" ;
    private final String COLON_EQUAL = this.COLON + this.EQUAL ;    // ":="
    private final String PARENTHESIS_OPEN = "(" ;
    private final String PARENTHESIS_CLOSE = ")" ;
    private final String COMMA = "," ;
    private final String END_FUZZIFY = "END_FUZZIFY" ;    
    private final String END_FUNCTION_BLOCK = "END_FUNCTION_BLOCK" ;
        
    public ArrayList<FunctionKind> functionDefinitions = new ArrayList<FunctionKind>();
    
    // Nomes expositivos dos tipos de função de pertinência aplicáveis aos conjuntos fuzzy (estadosFuzzy)
//    public static final String FUNC_NAME_DEFAULT = "Default" ;  // Se for default, permite a criação de 2 ou mais pontos
    
    public static final String FUNC_NAME_TRAPE = "Trapezoidal" ;      // Se for "trape", de trapezoidal, representa uma função com apenas 4 pontos
    public static final String FUNC_NAME_TRIAN = "Triangular" ;      // Se for "trian", de triangular, representa uma função com apenas 3 pontos
    public static final String FUNC_NAME_GAUSS = "Gaussiana" ;      // Se for "gauss", de gaussiana, representa uma função com apenas 2 pontos
    public static final String FUNC_NAME_GBELL = "Sino" ;      // Se for "gbell", de sino, representa uma função com apenas 3 pontos
    public static final String FUNC_NAME_SIGM = "Sigmoidal" ;        // Se for "sigm", de sigmoidal, representa uma função com apenas 2 pontos
    
    // Tipos de função de pertinência (na forma em que devem ser adicionados ao arquivo .fcl)
//    public static final String FUNC_TYPE_DEFAULT = "default" ;  // Se for default, permite a criação de 2 ou mais pontos
    
    public static final String FUNC_TYPE_TRAPE = "trape" ;      // Se for "trape", de trapezoidal, representa uma função com apenas 4 pontos
    public static final String FUNC_TYPE_TRIAN = "trian" ;      // Se for "trian", de triangular, representa uma função com apenas 3 pontos
    public static final String FUNC_TYPE_GAUSS = "gauss" ;      // Se for "gauss", de gaussiana, representa uma função com apenas 2 pontos
    public static final String FUNC_TYPE_GBELL = "gbell" ;      // Se for "gbell", de sino, representa uma função com apenas 3 pontos
    public static final String FUNC_TYPE_SIGM = "sigm" ;        // Se for "sigm", de sigmoidal, representa uma função com apenas 2 pontos  

//    private InputHandler reader ;
    
    private MainScreen mainScreen;
    
    public FuzzyFileFormatter()
    {
        this.setFunctionTypes();
    }

    public MainScreen getMainScreen()
    {
        return mainScreen;
    }

    public void setMainScreen(MainScreen mainScreen)
    {
        this.mainScreen = mainScreen;
    }

    
    
    public void setFunctionTypes()
    {
//        functionDefinitions.add(new FunctionKind (FUNC_NAME_DEFAULT, 2)); 
        
        functionDefinitions.add(new FunctionKind (FUNC_NAME_TRAPE, 4));
        functionDefinitions.add(new FunctionKind (FUNC_NAME_TRIAN, 3));
        functionDefinitions.add(new FunctionKind (FUNC_NAME_GAUSS, 2));
        functionDefinitions.add(new FunctionKind (FUNC_NAME_GBELL, 3));
        functionDefinitions.add(new FunctionKind (FUNC_NAME_SIGM, 2));
    }

    public ArrayList<FunctionKind> getFunctionDefinitions() {
        return functionDefinitions;
    }

    public void setFunctionDefinitions(ArrayList<FunctionKind> functionDefinitions) {
        this.functionDefinitions = functionDefinitions;
    }
    
        
    public String getTextFCL()
    {
        String s = "";
        String nLine = this.NEXT_LINE ;
        String spc = this.SPACE;
        String tab = this.TAB;
        String colon = this.COLON ;
        String semiCol = this.SEMICOLON ;
        String comma = this.COMMA ;
        String colEqual = this.COLON_EQUAL ;
        
        ArrayList<FuzzyEvid> fuzzyEvids = this.mainScreen.getFuzzyEvids();
        
        //  Monta o cabeçalho (primeira linha do arquivo)
        s += nLine + this.FUNCTION_BLOCK + spc + "VariaveisFuzzyDaHipotese_" +
            this.mainScreen.getHypNode().getHypNodeName() 
            + nLine ;
        
        // Espera montar a sessão de declaração de variáveis fuzzy 
        // Uma variável fuzzy para cada evidência bayesiana
        s += nLine + this.VAR_INPUT ;
        
        // Inicia a criação declaração das variáveis fuzzy (pega somente os nomes delas)
        for ( FuzzyEvid fuzzyE: fuzzyEvids)
        {
            s += nLine + tab + fuzzyE.getEvidName() + colon + spc + this.REAL + semiCol ;
        }
        // Indica o término da declaração de variáveis Fuzzy
        s += nLine + this.END_VAR + nLine ;      
        
        // Agora percorre todas as evidências e coleta cada um de seus estados
        for ( FuzzyEvid fuzzyE: fuzzyEvids)
        {
            // Coleta todos os estados da evidênia atual
            ArrayList<FuzzyState> fuzzyStates = fuzzyE.getFuzzyStates() ;
            
            // Nome da evidência
            s += nLine + this.FUZZIFY + spc + fuzzyE.getEvidName();
            
            // Percorre todos os estados da evidência atual
            for ( FuzzyState state : fuzzyStates)
            {
                // Descreve o seguinte: TERM nomeDoEstado := tipoDeFuncao
                s += nLine + tab + this.TERM + spc + state.getFuzzyStateName() + spc + colEqual + spc + state.getFuncTypeCode() ;
                
                // Armazena todos os pontos do estado atual
                ArrayList<Float> funcPoints = state.getFunctionPoints();
                
                // Acessa cada ponto e o acrescenta no output
                for ( float point : funcPoints)
                {
                    s += spc + point ;
                }
                // Terminou de ler os pontos do estado atual
                s += semiCol ;                
            }
            // Indica o término da leitura de uma variável fuzzy
            s += nLine + this.END_FUZZIFY + nLine ;            
        }
        // Neste ponto, é terminada a montagem do arquivo em formato .fcl após a leitura da última evidência/variável fuzzy
        s += nLine + this.END_FUNCTION_BLOCK + nLine ;        
        return s;
    }
    
    
}
