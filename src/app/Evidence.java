package app;


import java.util.ArrayList;


public class Evidence extends BayesianNode
{
    // um array dentro de outro: cada subArray armazena as probablidades dos estados da evidencia para cada estado da hipotese
    //private ArrayList<ArrayList<Double>> probsEvidenciaDadaHipotese = new ArrayList<ArrayList<Double>>();
    
   // private ArrayList<Double> probEstadosEvidencias = new ArrayList<Double>();
    
    // private Hipotese = hip ;
    
    private ArrayList<EvidenceState> evidenceStates = new ArrayList<>();
    
    private float measure ;
    
    public Evidence ()
    {
        super.setNodeType("Evidencia") ;
    }
    
    public Evidence (String name)
    {
        super.setNodeType("Evidencia") ;
        super.setNodeName(name);
    }
    
    public Evidence (String name, ArrayList<EvidenceState> newStates)
    {
        super.setNodeType("Evidencia") ;
        super.setNodeName(name);
        this.setEvidenceStates(newStates);
    }
    
    public Evidence (String name, EvidenceState newState)
    {
        super.setNodeType("Evidencia") ;
        super.setNodeName(name);
        this.evidenceStates.add(newState);
    }

    public ArrayList<EvidenceState> getEvidenceStates ()
    {
        return evidenceStates;
    }

    public void setEvidenceStates (ArrayList<EvidenceState> states)
    {
//        if ( ! states.isEmpty())
//        {            
//            System.out.println("----------------------------------------------------------------" 
//                    + states.get(0).showProbs_Evid_given_Hyp());
//        }
        this.evidenceStates = states;
    }    
    
    public void addEvidenceState (EvidenceState state)
    {
        this.evidenceStates.add(state);
    }
    
    //Retorna a posicao do ultimo estado adicionado na evidencia
    public int getLastStateIndex()
    {
        return this.getEvidenceStates().size() - 1 ;
    }
    
    //Retorna o ultimo estado adicionado na evidencia
    public EvidenceState getLastState ()
    {
        return this.getEvidenceStates().get(this.getLastStateIndex());
    }
    
    //Retorna um estado de evidencia dada uma posicao
    public EvidenceState getStateEvid(int i)
    {
        return this.getEvidenceStates().get(i);
    }
    
    public String showStates ()
    {
        String s = "" ;
        
        for (EvidenceState e : this.evidenceStates)
        {
            s += "\tEstado: " + e.getNameEvidState() + 
                    " --> P(e) = " + e.getProbEvidState() + 
                    "\n\t\t" + " --> P(e | Hi): " + e.showProbs_Evid_given_Hyp() + "\n";
        }
//        s = s.substring(0, s.length()-2);
        return s ;
    }
    
    public EvidenceState getStateByName(String name)
    {
        if ( !this.getEvidenceStates().isEmpty() )
        {
            for ( EvidenceState state : this.getEvidenceStates())
            {
                if ( state.getNameEvidState().equals(name))
                {
                    return state ;
                }
            }
        }
        return null ;
    }

    public float getMeasure()
    {
        return measure;
    }

    public void setMeasure(float measure)
    {
        this.measure = measure;
    }
    
    
    
    public void removeStateByName(String stateToRemove)
    {
        for ( EvidenceState state : this.evidenceStates )
        {
            if ( state.getNameEvidState().equals(stateToRemove))
            {
                this.evidenceStates.remove(state);
            }
        }
    }
    
    /*
    public ArrayList<ArrayList<Double>> getProbsEvidenciaDadaHipotese ()
    {
        return probsEvidenciaDadaHipotese;
    }

    public void setProbsEvidenciaDadaHipotese (ArrayList<ArrayList<Double>> probsEvidenciaDadaHipotese)
    {
        this.probsEvidenciaDadaHipotese = probsEvidenciaDadaHipotese;
    }        
    */

}
