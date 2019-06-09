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


public class FuzzyEvid
{    
    private FuzzyNode fuzzyNode ;
    
    private String evidName ;
    private ArrayList<FuzzyState> fuzzyStates = new ArrayList<FuzzyState>();
    private float measure ;
    
    public FuzzyEvid()
    {
                
    }

    public FuzzyNode getFuzzyNode()
    {
        return fuzzyNode;
    }

    public void setFuzzyNode(FuzzyNode node)
    {
        this.fuzzyNode = node;
    }

    public float getMeasure()
    {
        return this.measure;
    }

    public void setMeasure(float theMeasure)
    {
        this.measure = theMeasure;
    }    
    
    public FuzzyEvid(String evidName)
    {
        this.evidName = evidName;
    }

    public String getEvidName()
    {
        return evidName;
    }

    public void setEvidName(String evidName)
    {
        this.evidName = evidName;
    }

    public ArrayList<FuzzyState> getFuzzyStates()
    {
        return this.fuzzyStates;
    }

    public void setFuzzyStates(ArrayList<FuzzyState> fuzzyStates)
    {
        this.fuzzyStates = fuzzyStates;
    }
    
    public String showStates ()
    {
        String s = "\n" ;
        
        for (FuzzyState e : this.fuzzyStates)
        {
            s += "\tEstado: " + e.getFuzzyStateName() + 
                    " --> Function = " + e.getFunctionType() + 
                    "\n\t\t" + " --> Pontos: " + e.showPoints()+ "\n";
        }
//        s = s.substring(0, s.length()-2);
        return s ;
    }
    
}
