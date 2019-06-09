package app;

import java.util.ArrayList;


public class BayesianNode
{
    private String nodeType = "" ; //um nodo pode ser do tipo "Hipotese" ou "Evidencia"  
    private String nodeName = "" ;    
   // private ArrayList<String> estadosNodo = new ArrayList() ; //armazena os estados do nodo
   // private ArrayList<EstadoEvidencia> estados = new ArrayList() ; //armazena os estados do nodo
      
    public BayesianNode ()
    {
        
    }

    public String getNodeType ()
    {
        return nodeType;
    }

    public void setNodeType (String nodeType)
    {
        this.nodeType = nodeType;
    }

    public String getNodeName ()
    {
        return nodeName;
    }

    public void setNodeName (String nodeName)
    {
        this.nodeName = nodeName;
    }

    /*
    public ArrayList<String> getEstadosNodo ()
    {
        return estadosNodo;
    }

    public void setEstadosNodo (ArrayList<String> estadosNodo)
    {
        this.estadosNodo = estadosNodo;
    }    
    */    
}
