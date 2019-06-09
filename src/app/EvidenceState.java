package app;

import java.util.ArrayList;

public class EvidenceState 
{
    private String nameEvidState = "" ;
    
    private float probEvidState = 0f ;   // P(e) = Probabilidade do estado da evidencia "P(e)"  
        //> P(e) = Soma ( P(e | Hi) * P (Hi))
        //> Prob (som "baixo") = P(e1)
        //> P(e1) = P(e1 | H1) * P(H1) + P(e1 | H2) * P(H2) + P(e1 | H3) * P(H3) + P(e1 | H4) * P(H4)    
        
    private ArrayList<Float> probs_Evid_given_Hyp = new ArrayList<Float>();  //  P(e|Hi)
    
//    private ArrayList<Float> probs_Hyp_given_Evid = new ArrayList<Float>();  // P(Hi|e) -> Armazenaria resultados, mas como depende da consulta, nao foi usado
    
    // private Hipotese hipotese ;

    public EvidenceState ()
    {
    }
    
    public EvidenceState (String name)
    {
        this.nameEvidState = name.toLowerCase();
    }
    
    public EvidenceState (ArrayList<Float> probs)
    {
        this.probs_Evid_given_Hyp = probs ;
    }
    
    public EvidenceState (ArrayList<Float> probs, float probEvidState)
    {
        this.probs_Evid_given_Hyp = probs ;
        this.probEvidState = probEvidState ;
    }

    public String getNameEvidState ()
    {
        return nameEvidState;
    }

    public void setNameEvidState (String nameEvidState)
    {
        this.nameEvidState = nameEvidState;
    }

    public float getProbEvidState ()
    {
        return probEvidState;
    }

    public void setProbEvidState (float probEvidState)
    {
        this.probEvidState = probEvidState;
    }

    public ArrayList<Float> getProbs_Evid_given_Hyp ()
    {
        return probs_Evid_given_Hyp;
    }
    
    public float getProb_Evid_given_Hyp ( int i )
    {
        return this.getProbs_Evid_given_Hyp().get(i) ;
    }
    
    public float getLastProb_Evid_given_Hyp ()
    {
        return probs_Evid_given_Hyp.get(this.probs_Evid_given_Hyp.size()-1);
    }

    public void setProbs_Evid_given_Hyp (ArrayList<Float> probs_Evid_given_Hyp)
    {
        this.probs_Evid_given_Hyp = probs_Evid_given_Hyp;
    }
    
    public String showProbs_Evid_given_Hyp ()
    {
        String s = "";
        
        for ( float prob : this.probs_Evid_given_Hyp)
        {
            s += prob + ", " ;
        }
//        int m = s.length()-2;
//        s = s.substring(0, m);
        return s ;
    }

//    public ArrayList<Float> getProbs_Hyp_given_Evid ()
//    {
//        return probs_Hyp_given_Evid;
//    }
//
//    public void setProbs_Hyp_given_Evid (ArrayList<Float> probs_Hyp_given_Evid)
//    {
//        this.probs_Hyp_given_Evid = probs_Hyp_given_Evid;
//    }
    
    
}
