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
//	TERM escuro := (5,0) (90,1) (100,1);     // \n + $TERM + evid(i+1).state(j+1).name + $:= + #functionType + (@x1,y1) + (@x2,y2) + (@x3,y3) + $;    // #functionType = null (default), n(p) >=2 
//END_FUZZIFY                                     // \n + $END_FUZZIFY +
//                                                // \n +
//FUZZIFY espaco                                  // \n + $FUZZIFY + evid(i+n).name +
//	TERM apertado := (0, 1)  (80, 0) ;      // \n + $TERM + evid(i+n).state(j).name + $:= + #functionType + (@x1,y1) + (@x2,y2) + (@x3,y3) + $;    // #functionType = null (default), n(p) >=2 
//	TERM amplo := gauss 56 92;              // \n + $TERM + evid(i+n).state(j+1).name + $:= + @v1 + @v2 + $;    // #functionType = gauss, n(p) = 2
//END_FUZZIFY                                     // \n + $END_FUZZIFY +
//                                                // \n +
//END_FUNCTION_BLOCK                              // \n + $END_FUNCTION_BLOCK
package app;

import static app.FuzzyNode.FUNC_NAME_DEFAULT;
import static app.FuzzyNode.FUNC_NAME_GAUSS;
import static app.FuzzyNode.FUNC_NAME_GBELL;
import static app.FuzzyNode.FUNC_NAME_SIGM;
import static app.FuzzyNode.FUNC_NAME_TRAPE;
import static app.FuzzyNode.FUNC_NAME_TRIAN;
import java.util.ArrayList;

public class FuzzyState
{
    private String fuzzyStateName ;
    private String functionType ;
    private String funcTypeCode ;
    
//    private FunctionKind function ;   // não usado aqui
    
    private ArrayList<Float> functionPoints = new ArrayList<Float>();
//    private ArrayList<Float[]> defaultPoints = new ArrayList<Float[]>();

    public String getFuzzyStateName() {
        return fuzzyStateName;
    }

    public void setFuzzyStateName(String fuzzyStateName) {
        this.fuzzyStateName = fuzzyStateName;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType)
    {
        this.functionType = functionType;
        this.setFuncTypeCode(functionType);
    }

    public ArrayList<Float> getFunctionPoints() {
        return functionPoints;
    }

    public void setFunctionPoints(ArrayList<Float> functionPoints) {
        this.functionPoints = functionPoints;
    }
    
    public String showPoints ()
    {
        String s = "";
        
        for ( float point : this.functionPoints)
        {
            s += point + ", " ;
        }
//        int m = s.length()-2;
//        s = s.substring(0, m);
        return s ;
    }
    
    public void setFuncTypeCode(String typeName)
    {
        switch (typeName)
        {
            case FUNC_NAME_DEFAULT :
            {                
                this.funcTypeCode = "" ;
                break;
            }
            case FUNC_NAME_TRIAN :
            {
                this.funcTypeCode = "trian" ;
                break;
            }
            case FUNC_NAME_TRAPE :
            {
                this.funcTypeCode = "trape" ;
                break;
            }
            case FUNC_NAME_GAUSS :
            {
                this.funcTypeCode = "gauss" ;
                break;
            }
            case FUNC_NAME_GBELL :
            {
                this.funcTypeCode = "gbell" ;
                break;
            }
            case FUNC_NAME_SIGM :
            {
                this.funcTypeCode = "sigm" ;
                break;
            }
            default :
                break;
        }
    }
    
    public String getFuncTypeCode()
    {
        return this.funcTypeCode ;
    }
    

//    public ArrayList<Float[]> getDefaultPoints() {
//        return defaultPoints;
//    }
//
//    public void setDefaultPoints(ArrayList<Float[]> defaultPoints) {
//        this.defaultPoints = defaultPoints;
//    }
//    
    
}
