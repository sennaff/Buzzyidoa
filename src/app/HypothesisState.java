package app;

public class HypothesisState
{
    private String nameHypState = "" ;
    
    private float prob_Hyp = 0f ;
    
    public HypothesisState()
    {
        
    }
    
    public HypothesisState(String name)
    {
        this.nameHypState = name ;
    }
    
    public HypothesisState(float prob)
    {
        this.prob_Hyp = prob ;
    }
    
    public HypothesisState(String name, float prob)
    {
        this.nameHypState = name ;
        this.prob_Hyp = prob ;
    }

    public String getNameHypState ()
    {
        return nameHypState;
    }

    public void setNameHypState (String name)
    {
        this.nameHypState = name;
    }

    public float getProb_Hyp ()
    {
//        return (float)this.prob_Hyp;
        return this.prob_Hyp;
    }

    public void setProb_Hyp (float prob)
    {
//        this.prob_Hyp = (float)prob;
        this.prob_Hyp = prob;
    }    
}
