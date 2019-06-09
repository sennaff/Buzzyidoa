package app;

import java.util.ArrayList;

public class Hypothesis extends BayesianNode
{  
    // armazena as probablidades de um nodo de hipotese
    //private ArrayList<Double> probsHipotese = new ArrayList<>() ;
    
    private ArrayList<HypothesisState> hypStates = new ArrayList<>();
    
    private ArrayList<Evidence> bayesEvids = new ArrayList<>();    
    
    // construtor que já define o tipo a partir do atributo constante na superclasse
    public Hypothesis ()
    {
        super.setNodeType("Hipotese") ;
    }

    public ArrayList<HypothesisState> getHypStates ()
    {
        return hypStates;
    }

    public void setHypStates (ArrayList<HypothesisState> hypStates)
    {
        this.hypStates = hypStates;
    }
    
    public void addHypState (HypothesisState state)
    {
        this.hypStates.add(state) ;
    }
    
    public int getNumStates ()
    {
        return this.getHypStates().size();
    }
    
    public HypothesisState getHypState (int i)
    {
        return this.getHypStates().get(i) ;
    }
    
    //Retorna a posicao de um determinado estado dentro do conjunto de estados da hipotese
    public int getStateIndex (HypothesisState state)
    {
        int index = -1;
        for (HypothesisState s : this.getHypStates())
        {
            if (s.getNameHypState().equals(state.getNameHypState()))
            {
                index = this.getHypStates().indexOf(s);
            }
        }
        return index ;                
    }

    public ArrayList<Evidence> getBayesEvids() {
        return bayesEvids;
    }

    public void setBayesEvids(ArrayList<Evidence> evidences) {
       
        this.bayesEvids = evidences;
        //atualizar prob evi
    }
    
    //Associa uma nova evidencia com a hipotese
    public void addEvidence (Evidence evid)
    {
        // adiciona nova evidencia
        this.bayesEvids.add(evid);
    }
    
    //Retorna a posicao da ultima evidencia adicionada na hipotese
    public int getLastEvidenceIndex ()
    {
        return this.bayesEvids.size() - 1 ;
    }
    
    // Retorna a ultima evidencia adicionada na hipotese
    public Evidence getLastEvidence ()
    {
        return this.bayesEvids.get(this.getLastEvidenceIndex()) ;
    }
    
    //Retorna uma evidencia dada uma posicao
    public Evidence getEvidence(int i)
    {
        return this.bayesEvids.get(i);
    }
    
    // Retorna um conjunto de evidencias com estados completos,
    // a partir de um conjunto de evidencias com estados faltantes
    public ArrayList<Evidence> getFullStateEvids (ArrayList<Evidence> inputEvids)
    {
        ArrayList<Evidence> fullEvids = new ArrayList<Evidence>();
        for (Evidence evi : inputEvids)
        {
            for (Evidence evi2 : this.getBayesEvids())
            {
                if (evi.getNodeName().equals(evi2.getNodeName()))
                {
                    fullEvids.add(evi2);
                }
            }
        }        
        return fullEvids ;
    }
    
    public String showStates ()
    {
        String s = "\nHipótese '" + this.getNodeName() + "' / Número de estados: " + 
                   this.getHypStates().size() + "\n" ;
        
        for (HypothesisState e : this.hypStates)
        {
            s += "\tEstado: " + e.getNameHypState()+ " --> P(Hi): " + e.getProb_Hyp()+ "\n";
        }
        s += "\n" ;
        return s ;
    }
    
    public String showEvidences ()
    {
        String s = "\nNúmero de evidências :" + this.getBayesEvids().size() + "\n\n" ;
        
        for (Evidence e : this.bayesEvids)
        {
            s += "Evidência '" + e.getNodeName() + "' / Número de estados: " + 
                 e.getEvidenceStates().size() + "\n" + e.showStates() + "\n";
        }        
        return s ;
    }
    
    public String showSortedEvidencesNames ()
    {
        String s = "" ;       
        
        int cont = 0 ;
        for (Evidence e : this.bayesEvids)
        {
            s += cont + " - " + e.getNodeName() + "\n";
            cont++;
        }        
        return s ;
    }
    
    // ??????
    public void atualizaProbEvidencias ()
    {/*
        for ( Evidence evi : this.bayesEvids )
        {
           // double somaEvidencia 
            
        for ( Double prob: this.probsHipotese )
        {
            
        }
            //evi.setProb
        }
        */
    }
    
    
}
