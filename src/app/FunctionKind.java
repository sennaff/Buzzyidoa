
package app;

public class FunctionKind
{
    private String nameFunc ;   // Nome do tipo de função
    private String typeFunc ;   // Nome do tipo de função
    private int numOfPoints ;   // Quantidade de pontos para o tipo de função (se for default, representa o mínimo de pontos)
    
    // Nomes expositivos dos tipos de função de pertinência aplicáveis aos conjuntos fuzzy (estadosFuzzy)
    public static final String FUNC_NAME_DEFAULT = "Default" ;  // Se for default, permite a criação de 2 ou mais pontos
    public static final String FUNC_NAME_TRIAN = "Triangular" ;      // Se for "trian", de triangular, representa uma função com apenas 3 pontos
    public static final String FUNC_NAME_TRAPE = "Trapezoidal" ;      // Se for "trape", de trapezoidal, representa uma função com apenas 4 pontos
    public static final String FUNC_NAME_GAUSS = "Gaussiana" ;      // Se for "gauss", de gaussiana, representa uma função com apenas 2 pontos
    public static final String FUNC_NAME_GBELL = "Sino" ;      // Se for "gbell", de sino, representa uma função com apenas 3 pontos
    public static final String FUNC_NAME_SIGM = "Sigmoidal" ;        // Se for "sigm", de sigmoidal, representa uma função com apenas 2 pontos
    
    // Tipos de função de pertinência (na forma em que devem ser adicionados ao arquivo .fcl)
    public static final String FUNC_TYPE_DEFAULT = "default" ;  // Se for default, permite a criação de 2 ou mais pontos
    public static final String FUNC_TYPE_TRIAN = "trian" ;      // Se for "trian", de triangular, representa uma função com apenas 3 pontos
    public static final String FUNC_TYPE_TRAPE = "trape" ;      // Se for "trape", de trapezoidal, representa uma função com apenas 4 pontos
    public static final String FUNC_TYPE_GAUSS = "gauss" ;      // Se for "gauss", de gaussiana, representa uma função com apenas 2 pontos
    public static final String FUNC_TYPE_GBELL = "gbell" ;      // Se for "gbell", de sino, representa uma função com apenas 3 pontos
    public static final String FUNC_TYPE_SIGM = "sigm" ;        // Se for "sigm", de sigmoidal, representa uma função com apenas 2 pontos 
    

    public FunctionKind(String nameFunc, int numOfPoints)
    {
        this.nameFunc = nameFunc;        
        this.numOfPoints = numOfPoints;
    }

    public String getNameFunc() {
        return nameFunc;
    }

    public void setNameFunc(String nameFunc) {
        this.nameFunc = nameFunc;
    }

    public String getTypeFunc() {
        return typeFunc;
    }

    public void setTypeFunc(String typeFunc) {
        this.typeFunc = typeFunc;
    }

    public int getNumOfPoints() {
        return numOfPoints;
    }

    public void setNumOfPoints(int numOfPoints) {
        this.numOfPoints = numOfPoints;
    }
    
    public void setFuncType(String funcName)
    {
        switch (funcName)
        {
            case FUNC_NAME_DEFAULT :
            {
                this.typeFunc = FUNC_TYPE_DEFAULT ;
                break;
            }
            case FUNC_NAME_TRIAN :
            {
                this.typeFunc = FUNC_TYPE_TRIAN ;
                break;
            }
            case FUNC_NAME_TRAPE :
            {
                this.typeFunc = FUNC_TYPE_TRAPE ;
                break;
            }
            case FUNC_NAME_GAUSS :
            {
                this.typeFunc = FUNC_TYPE_GAUSS ;
                break;
            }
            case FUNC_NAME_GBELL :
            {
                this.typeFunc = FUNC_TYPE_GBELL ;
                break;
            }
            case FUNC_NAME_SIGM :
            {
                this.typeFunc = FUNC_TYPE_SIGM ;
                break;
            }
            default :
                break;
        }
        
//        System.out.println("\n ~/~/~/~/~/~ TIPO DE FUNÇÃO DEFINIDO:" + this.typeFunc);
    }

    
    
    
    
}
