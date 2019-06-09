package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import org.antlr.runtime.RecognitionException;

public class InputHandler
{
    //texto lido de um arquivo
    private String textInputRaw = "" ;
    
    //texto resultante da remoção de espaços do conteúdo de um arquivo lido
    private String textInput = "";
    
    private String textInputBayes = "" ; //valores de entrada da parte bayesiana
        
    private String fileName = "" ;   // guarda temporariamente o nome do arquivo sendo lido no momento
    
    private int readerState = 0 ;  /* Serve para indicar ao leitor em que etapa da criação de um nodo ele está, 
                                        durante a leitura de um arquivo .txt de entrada.
    
                        Significados dos estados do leitor:
    
                                        0 = Definição do tipo do nodo (hipótese ou evidência)
                                        1 = Definição do nome do nodo
                                        2 = Definição dos nomes dos estados do nodo
                                        3 = Definição das probabilidades de cada estado de um nodo
                                        4 = Fechamento do bloco de um nodo    
                                    */    
       
    private Hypothesis hypothesis ; // guarda a hipotese carregada do arquivo
                                                // A hipotese funciona como centro do programa,
                                                // conhecendo todas as evidencias e permitindo a execucao dos calculos
    
    //"Fuzzy Interface System", é objeto responsável pela manipulação de toda lógica fuzzy envolvida na aplicação
    //É inicializado somente quando necessário, durante a chamada do método "readFuzzyFile"
    FIS fuzzyHandler ;
    
//    private FuzzyFileFormatter fclCreator ;
    private MainScreen mainScreen ;
    
    
    //construtor simples
    public InputHandler()
    {
        this.hypothesis = new Hypothesis();        
    }

    public MainScreen getMainScreen() {
        return mainScreen;
    }

    public void setMainScreen(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

  
// -----------------------------~~~~~~~~~~~~~~~------------------------------------- //
// -----------------------------~~~~~~~~~~~~~~~------------------------------------- //    
// ----- SESSAO DE METODOS PARA ENTRADA DE ARQUIVOS (Parte bayesiana do input) ----- //
// ------                                                                     ------ //    
// -------                                                                   ------- //
    
    //Por padrão, carrega arquivos localizados na pasta do projeto.
    //Armazena o conteúdo do arquivo de entrada no atributo String "textoInput"
    public void loadTextFile(String fileName)
    {

//        CODIGOS PARA LEITURA DE ARQUIVO COM INPUT Na saída
//        Scanner ler = new Scanner(System.in);
//
//        //Deve-se inserir o nome do arquivo e o tipo, ex: arquivoLeitura.txt
//        System.out.printf("Informe o nome de arquivo texto:\n");
//        String nome = ler.nextLine(); //pega o nome do arquivo digitado pelo usuário
        
        String name = fileName; //pega o nome do arquivo digitado pelo usuário
        
        this.fileName = name ;
        this.textInputRaw = "" ;
        
        try
        {
            //funções de acesso e leitura do arquivo
            System.out.println("nome do arquivo no leitor" + name);
            FileReader file = new FileReader(name);
            BufferedReader readFile = new BufferedReader(file);

            String line = "\n" ; 
            while (line != null)
            {                
                line = readFile.readLine();/* lê cada linha do arquivo, e a variável "line" 
                                            recebe o valor "null" quando o processo
                                            de repetição atingir o final do arquivo texto */ 
                if (line != null)
                {
                    this.textInputRaw += line + "\n" ;    // this.textoInputRaw += line + "\n" ; 
                }                                   // se o comando for feito dessa forma, o texto vai
                                                    // ficar armazenado com as quebras de linha
            }
            // Neste ponto, o atributo "textoInputRaw" guarda extamente o mesmo conteúdo do arquivo texto,
            // podendo (ou não) conter as quebras de linha - por default, mantém as quebras.
            
            // Mostra o exato conteúdo do arquivo de entrada
            
            System.out.println("\nConteúdo do arquivo texto (" + name + "):\n");
            System.out.println(textInputRaw + "\n");
                        
            // Remove todos os espaçamentos ou quebras de linha existentes no texto de entrada
            this.textInput = this.removeAllSpaces(textInputRaw);
            
            // Mostra o texto obtido do arquivo, mas sem espaçamentos ou quebras de linha, para permitir o processamento
            //System.out.println("Texto processado obtido do arquivo de entrada (" + nome + "):\n");
            //System.out.println(textoInput + "\n");            

            //Encerra a leitura do arquivo
            file.close();
            readFile.close();
        }
        catch (IOException e)   // Pode ocorrer uma exceção caso tente-se efetuar a leitura de um arquivo inexistente
        {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }        
    }
    
    // Método que remove todos os espaçamentos de um arquivo
    public String removeAllSpaces (String in)
    {
        String s = in ;
        s = this.removeBorderSpaces(s);
        s = this.removeSpaces(s) ;
        s = this.removeBreakLines(s) ;        
        return s ;
    }
    
    //Aplica o método "trim()" ao string desejado, apagando espaços antes e depois do texto.
    public String removeBorderSpaces(String in)
    {
        String out = "" ;
        out = in.trim();
        return out ;
    }
    
    // Remove todos os espaçamentos de um texto String
    public String removeSpaces (String in)
    {
        String s = in ;
        s = s.replaceAll(" ", "");
        s = s.replaceAll(" ", "");
        s = s.replaceAll("\r", "");
        s = s.replaceAll("\t", "");
        return s ;
    }
    
    // Remove todas as quebras de linha de um texto String
    public String removeBreakLines (String in)
    {
        String s = in ;
        s = s.replaceAll("\n", "");        
        String breakLine = System.getProperty("line.separator");
        s = s.replaceAll(breakLine, "");
        return s ;
    }
    
    //Método que percorre o texto de entrada em busca dos parâmetros de criação de nodos Bayesianos
    public boolean readBayes()
    {
        // Define o input a ser tratado como entrada bayesiana
        this.textInputBayes = this.textInput ;
        
        // Garante a inicialização correta do estado do leitor
        this.readerState = 0 ;
        
        System.out.println ("Quantidade de caracteres no texto processado: " + this.textInputBayes.length() + "\n") ;                     
        
        //guarda temporariamente o tipo do nodo lido no momento atual        
        String nodeType = "" ;  // um nodo pode ser do tipo "Hypothesis" ou "Evidence"
                
        if (this.textInputBayes.length() > 0)   // verifica se o arquivo de entrada não está em branco
        {        
            for (int i=0 ; i < this.textInputBayes.length() ; i++) // Iterador que percorrerá o texto de entrada
            {                                                      // Irá definir os parâmetros de criação dos nodos                  
               // System.out.println ("Iteração - " + i + " / EstadoLeitor Atual: " + this.estadoLeitor + "\n") ;
                switch (this.readerState)
                {
                    case 0 :    //espera definir o tipo de nodo a ser criado
                    {
                        nodeType = "" ; //garante a limpeza da variável para reconhecer um novo nodo
                        
                        // É verificado se o identificador de tipo corresponde a "hipotese"
                        if (this.textInputBayes.substring(i, i+9).equalsIgnoreCase("BEGIN_HIP"))
                        {
                            nodeType = "Hipotese" ;
                            i += 9 ;    // aponta o cursor do leitor para o primeiro caractere após o id do tipo de nodo                          
                            
                            // Neste ponto, é esperado que seja encontrado um token de mudança de estado do leitor (;)
                            // A execução irá sair do "SWITCH"
                        }
                        else    // Como nao foi encontrado o identificador de tipo "hipotese",
                        {       // espera-se que seja um nodo de evidencia
                            if (this.textInputBayes.substring(i, i+9).equalsIgnoreCase("BEGIN_EVI"))
                            {
                                nodeType = "Evidencia" ;
                                i += 9 ;    // aponta o cursor do leitor para o primeiro caractere após o id do tipo de nodo
                                
                                // Neste ponto, é esperado que seja encontrado um token de mudança de estado do leitor (;)
                                // A execução deverá sair do "SWITCH"
                            }
                            else    // Se a execução entrar neste bloco, significa que houve um erro na formatação do arquivo,
                            {       // fazendo com que o programa sinalize o problema.
                                System.out.println ("\nERRO-01: Tipo de nodo inválido - " 
                                                        + this.textInputBayes.substring(i, i+9)
                                                        +   " (i = " + i + ")\n") ;
                            }
                        }
                        break ;
                    }

                    case 1 :    //espera definir o nome do nodo a ser criado
                    {
                        String nodeName = "" ; //garante a limpeza da variável para reconhecer um novo nodo
                        
                        while (this.textInputBayes.charAt(i) != ';') //grava o nome do nodo até ser definido seu fim
                        {
                            nodeName += this.textInputBayes.charAt(i);
                            i++ ;
                        }
                        if (nodeType.equals("Hipotese"))
                        {
                            this.hypothesis.setNodeName(nodeName);
                        }
                        else
                        {
                            if (nodeType.equals("Evidencia"))
                            {
                                Evidence newEvidence = new Evidence (nodeName) ;
                                this.hypothesis.addEvidence(newEvidence);
                            }
                        }                        
                        break ;
                    }

                    case 2 :    //espera definir os nomes dos estados do nodo a ser criado
                    {
                        String stateName = "" ;    // armazena temporariamente o nome de um novo estado
                        
                        boolean readStates = true ;

                        while ( readStates == true ) //aguarda o fim da declaração de estados
                        {
                            //grava o nome do estado até ser definido seu fim
                            if  ( ( this.textInputBayes.charAt(i) != ',' ) && (this.textInputBayes.charAt(i) != ';') )
                            {
                                stateName += this.textInputBayes.charAt(i);
                            }                            
                            else
                            {                                
                                if (nodeType.equals("Hipotese"))
                                {   
                                    //System.out.println ("Novo estado adicionado ao nodo '" + this.hipotese.getNodeName() + "': " + nomeEstado +"\n") ;                                    
                                    
                                    //cria um novo estado para a hipotese apenas com o nome dele
                                    HypothesisState newHypState = new HypothesisState(stateName);
                                    
                                    // adiciona o estado da hipotese ao conjunto de estados
                                    this.hypothesis.addHypState(newHypState);
                                    
                                    stateName = "" ;   //limpa a variavel para permitir leituras posteriores
                                }
                                else
                                {
                                    if (nodeType.equals("Evidencia"))
                                    {
                                        //cria um novo estado de evidencia a partir do novo nome lido
                                        EvidenceState newEvidState = new EvidenceState(stateName);                                        
                                                                                
                                        //guarda temporariamente a ultima evidencia adicionada na hipotese
                                        Evidence currentEvid = this.hypothesis.getLastEvidence();                                        
                                        
                                        //adiciona o novo estado de evidencia na evidencia temporaria
                                        currentEvid.addEvidenceState(newEvidState) ;
                                        
                                        //guarda a posicao da ultima evidencia adicionada, que deve ser atualizada
                                        int lastEvidence = this.hypothesis.getLastEvidenceIndex();
                                        
                                        //substitui a ultima evidencia da hipotese pela nova, que contem o novo estado adicionado
                                        this.hypothesis.getBayesEvids().set(lastEvidence, currentEvid);
                                        
                                        //System.out.println("Estado de evidencia ADICIONADO! = " + nomeEstado);
                                        
                                        stateName = "" ;   //limpa a variavel para permitir leituras posteriores
                                    }
                                    else
                                    {
                                        System.out.println ("ERRO ao adicionar estado do nodo - Tipo de nodo invalido.\n") ;
                                    }
                                }
                                if (this.textInputBayes.charAt(i) == ';')   // encontrou o final da criação de estados
                                {
                                    readStates = false ;
                                    i-- ;                                    
                                    /*
                                        - OBS-0:
                                        - talvez as duas linhas acima possam ser substituidas por um "break ;"
                                        - pois isso deveria evitar o incremento "i++" antes do encerramento do "while"
                                    */
                                }                                
                            }
                            i++ ; // -> OBS-0
                        }

                        // Se executar o bloco a seguir, houve um probelma na criacao dos estados para o nodo atual
                        // pois o nodo atual nao contem estados
                        if ((this.hypothesis.getHypStates().size() < 1) && 
                                (this.hypothesis.getLastEvidence().getEvidenceStates().size() < 1))
                        {
                            System.out.println ("ERRO: Não foi possível definir estados para o nodo de '" + nodeType + "'.\n") ;
                            return false;    //Encerra a leitura do arquivo
                        }
                        break ;
                    }
                    
                    case 3 :    //espera definir as probabilidades de cada estado do nodo a ser criado
                    {           //O funcionamento nos nodos de hipótese é diferente dos nodos de evidências
                        if (nodeType.equals("Hipotese"))
                        {                            
                            boolean readProbs = true ;  //indicador de continuidade da leitura
                            
                            String newProb = "" ;      //armazena temporariamente uma probabilidade lida
                            
                            //contador para determinar a qual estado pertence a probabilidade lida no momento
                            int currentHypState = 0 ;
                            
                            while ( readProbs == true ) //aguarda o fim da definição das probabilidades
                            {
                                //grava cada probabilidade separadamente
                                if  ( ( this.textInputBayes.charAt(i) != ',' ) && (this.textInputBayes.charAt(i) != ';') )
                                {
                                    newProb += this.textInputBayes.charAt(i);
                                    //System.out.println (" - NovaProb - DENTRO DO IF (hipotese) - (" + i + "): " + novaProb) ;
                                }                            
                                else
                                {
                                    try // trata uma possível exceção causada pela entrada de uma probabilidade inválida
                                    {                                        
                                        //System.out.println (" - NovaProb - Iteracao (" + i + "): " + novaProb + "\n") ;
                                        
                                        float prob = Float.parseFloat(newProb); // converte o String lido para um formato numérico

                                        //Pega um estado da hipotese
                                        HypothesisState updateState = this.hypothesis.getHypStates().get(currentHypState);
                                                                                
                                        updateState.setProb_Hyp(prob);  //Adiciona a probabilidade ao estado
                                        
                                        //Atualiza o estado armazenado na hipotese
                                        this.hypothesis.getHypStates().set(currentHypState, updateState);
                                        
                                        newProb = "";  // prepara o leitor para ler uma nova probabilidade
                                        
                                        currentHypState++ ; // Aponta o proximo estado a receber uma probabilidade

                                        if (this.textInputBayes.charAt(i) == ';')
                                        {                                             
                                            readProbs = false ; // determina que nao se espera mais probabilidades para o nodo atual
                                            i-- ;               // corrige a posicao do cursor, considerando que ela vai ser incrementada                                            
                                            /*
                                                - OBS-1:
                                                - talvez as duas linhas acima possam ser substituidas por um "break ;"
                                                - pois isso deveria evitar o incremento "i++" antes do encerramento do "while"
                                            */                                            
                                            System.out.println("\n\n-------\n\n" + this.hypothesis.showStates() + "-------\n\n");
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        System.out.println ("\nERRO: Probabilidade inválida de hipotese - " + e.getMessage() 
                                                            + " - NovaProb: " + newProb 
                                                            + "\n - Char at [" + i + "]: " + this.textInputBayes.charAt(i)
                                                            + "\n Estado leitor: " + this.readerState) ;
                                        return false;    //encerra a leitura do arquivo
                                    }
                                }
                                i++ ;   // incremento citado na "OBS-1"
                            }
                        }         
                        else
                        {   // Espera ler as probabilidades de cada estado do nodo de evidencia atual
                            if (nodeType.equals("Evidencia"))
                            {                                
                                int actualEvidState = 0 ;   // aponta para o estado recebendo probabildiades no momento                                
                                
                                //array temporario para armazenar os conjuntos de probabilidades de cada estado da evidencia                            
                                ArrayList<Float> probsPerState = new ArrayList<Float>();           
                                
                                boolean readProbs = true ;  //Indica que deve-se continuar a leitura de probabilidades
                                
                                String newProb = "" ;      //armazena temporariamente uma probabilidade lida

                                while ( readProbs == true ) //aguarda o fim da definição das probabilidades
                                {
                                    char actualChar = this.textInputBayes.charAt(i); //armazena o caracere atual para comparacao
                                    
                                    //verifica se ainda deve continuar montando a probabilidade atual
                                    if  ( ( actualChar != ',' ) && (actualChar != '#') && (actualChar != ';') )
                                    {
                                        newProb += this.textInputBayes.charAt(i);
                                        
                                        //System.out.println (" - NovaProb - DENTRO DO IF (Evidence) - (" + i + "): " + novaProb) ;
                                    }                            
                                    else
                                    {
                                        try // trata uma possível exceção causada pela entrada de uma probabilidade inválida
                                        {                                            
                                            //System.out.println (" - NovaProb - Iteracao (" + i + "): " + novaProb + "\n") ;
                                            
                                            float prob = Float.parseFloat(newProb); // converte o String lido para um formato numérico
                                            probsPerState.add(prob);                // armazena a nova probabilidade lida
                                            newProb = "";                           // prepara o leitor para ler uma nova probabilidade
                                            
                                            //Adiciona as novas probabilidades lidas ao estado correspondente
                                            if ( actualChar == '#')  // Esse separador "#" indica que as probs de um estado terminaram,
                                            {                        // e que ainda existem probs de 1 ou mais estados a serem lidas
                                                
                                                //guarda temporariamente a ultima evidencia adicionada na hipotese
                                                Evidence actualEvid = this.hypothesis.getLastEvidence();
                                                
                                                //Vai armazenar o valor da probabilidade do estado da evidencia "p(e)" apos calculado
                                                float probEvidState = 0f;    // P(e)
                                                
                                                //Calcula a probabilidade do estado da evidencia "P(e)" -> vide classe "EvidenceState"
                                                for (int m = 0 ; m < probsPerState.size() ; m++)
                                                {
                                                    probEvidState += probsPerState.get(m) * this.hypothesis.getHypStates().get(m).getProb_Hyp() ;
                                                }                                                
                                                //Armazena o estado que deve receber as probabilidades no momento
                                                EvidenceState currentEvidState = actualEvid.getEvidenceStates().get(actualEvidState);
                                                
                                                //Associa o P(e) calculado ao estado atual
                                                currentEvidState.setProbEvidState(probEvidState);
                                                
                                                //Associa o array de probabilidades ao estado atual
                                                currentEvidState.setProbs_Evid_given_Hyp(probsPerState);
                                                
                                                //Coloca o estado atual na evidencia atual
                                                actualEvid.getEvidenceStates().set(actualEvidState, currentEvidState);
                                                
                                                //Atualiza a evidencia atual na hipotese
                                                this.hypothesis.getBayesEvids().set(this.hypothesis.getLastEvidenceIndex(), actualEvid);
                                                
                                                actualEvidState++;  // Aponta para o proximo estado que deve receber probabilidades
                                                                                                
                                                //Limpa a variavel para ler as probabilidades do estado seguinte
                                                probsPerState = new ArrayList<Float>() ;   
                                                
                                                //Variavel para teste
                                                Evidence evid = this.hypothesis.getLastEvidence();
                                                
                                                System.out.println ("\n\n> Evidencia '" + evid.getNodeName() + 
                                                                    "' - Estado: '" + evid.getStateEvid(0).getNameEvidState() +
                                                                    "' - Prob do estado da Evidencia: " + evid.getStateEvid(0).getProbEvidState() + 
                                                                    "\n - Probs dada a Hipotese: " + evid.getStateEvid(0).showProbs_Evid_given_Hyp() + "\n") ;                                                
                                            }
                                            if ( actualChar == ';') // o separador ";" aqui, indica que acabou a leitura de probabilidades
                                            {
                                                //guarda temporariamente a ultima evidencia adicionada na hipotese
                                                Evidence actualEvidence = this.hypothesis.getLastEvidence();
                                                
                                                //Vai armazenar o valor da probabilidade do estado da evidencia "p(e)" apos calculado
                                                float probEvidState = 0f;    // P(e)
                                                
                                                //Calcula a probabilidade do estado da evidencia "P(e)" -> vide classe "EvidenceState"
                                                for (int m = 0 ; m < probsPerState.size() ; m++)
                                                {
                                                    probEvidState += probsPerState.get(m) * this.hypothesis.getHypStates().get(m).getProb_Hyp() ;
                                                }                                          
                                                //Armazena o estado que deve receber as probabilidades no momento
                                                EvidenceState currentEvidState = actualEvidence.getEvidenceStates().get(actualEvidState);
                                                
                                                //Associa o P(e) calculado ao estado atual
                                                currentEvidState.setProbEvidState(probEvidState);
                                                
                                                //Associa o array de probabilidades ao estado atual
                                                currentEvidState.setProbs_Evid_given_Hyp(probsPerState);
                                                
                                                //Coloca o estado atual na evidencia atual
                                                actualEvidence.getEvidenceStates().set(actualEvidState, currentEvidState);
                                                
                                                //Atualiza a evidencia atual na hipotese
                                                this.hypothesis.getBayesEvids().set(this.hypothesis.getLastEvidenceIndex(), actualEvidence);
                                                
                                                actualEvidState++;  // Aponta para o proximo estado que deve receber probabilidades
                                                                                                
                                                //Limpa a variavel para ler as probabilidades do estado seguinte
                                                probsPerState = new ArrayList<Float>();
                                                
                                                //Variaveis para teste
//                                                Evidence evid = this.hypothesis.getLastEvidence();
////                                                float sumProbsEvidStates = evid.getStateEvid(0).getProbEvidState() + evid.getStateEvid(1).getProbEvidState(); // causa erro caso haja só 1 estado na evidencia
//                                                
//                                                System.out.println ("\n\n> Evidencia '" + evid.getNodeName() + 
//                                                                    "' - Estado: '" + evid.getLastState().getNameEvidState() +
//                                                                    "' - P(E): " + evid.getLastState().getProbEvidState() + 
//                                                                    "\n - Probs dada a Hipotese: " + evid.getLastState().showProbs_Evid_given_Hyp());
//                                                                    //"\n - Soma das probs da evidência: " + somaProbsEstadosEvid) ;                                                
                                                readProbs = false ;
                                                i-- ;                                                
                                                /*
                                                    - OBS-2:
                                                    - talvez as duas linhas acima possam ser substituidas por um "break ;"
                                                    - pois isso deveria evitar o incremento "i++" antes do encerramento do "while"
                                                */
                                            }
                                        }
                                        catch (Exception e)
                                        {
                                            // WARNING - Aviso de erro
                                            System.out.println ("ERRO: Probabilidade inválida (" + e.getMessage() + ")" +
                                                                " - Char Atual: (" + actualChar + ") ~ NewProb = (" + newProb + ")" +
                                                                " ~ 3 chars anteriores: (" + this.textInputBayes.charAt(i-4) +
                                                                this.textInputBayes.charAt(i-3) +
                                                                this.textInputBayes.charAt(i-2) +
                                                                this.textInputBayes.charAt(i-1) + ")") ;
                                            return false;    //encerra a leitura do arquivo
                                        }
                                    }
                                    i++ ;   // Incremento citado na "OBS-2" 
                                }
                            }
                            else
                            {
                                System.out.println ("ERRO: Tipo de nodo inválido - leitura de probabilidade interrompida.") ;
                                return false;    //encerra a leitura do arquivo
                            }
                        }
                        break ; 
                    }
                    
                    case 4 :    // espera-se o fechamento do bloco de criação de um nodo
                    {   //encerrando bloco de hipotese?
                        if (this.textInputBayes.substring(i, i+7).equalsIgnoreCase("END_HIP")) 
                        {                            
                            i += 7 ; // aponta o cursor do leitor para o primeiro caractere após o id de fechamento de bloco                            
                            // Neste ponto, é esperado que seja encontrado um token de mudança de estado do leitor (;)
                            // A execução deverá sair do "SWITCH"
                        }
                        else
                        {   //encerrando bloco de evidencia?
                            if (this.textInputBayes.substring(i, i+7).equalsIgnoreCase("END_EVI"))
                            {
                                // aponta o cursor do leitor para o primeiro caractere após o id de fechamento de bloco
                                i += 7 ;                                                                
                                // Neste ponto, é esperado que seja encontrado um token de mudança de estado do leitor (;)
                                // A execução deverá sair do "case" atual
                            }
                            else
                            {
                                System.out.println ("ERRO: marcador inválido para encerramento de bloco de nodo.") ;
                                break ;
                            }
                        }
                        break ;
                    }

                    default :   // se executar este bloco, houve uma falha no controle do 'estadoLeitor'
                    {
                        System.out.println ("ERRO: Valor inválido para o 'estadoLeitor' - durante a validação.") ;
                        break ;
                    }
                }

                //MUDANÇA DE ESTADO DO LEITOR - Neste ponto da execução, é sempre esperado encontrar o caractere ";"
                if (this.textInputBayes.charAt(i) == ';') // muda o estado do leitor ao achar um separador (;)
                {   // verifica se estadoLeitor está no intervalo [0,3]
                    if ( (0 <= this.readerState) && (this.readerState < 4 ))
                    {
                        this.readerState++;                    
                    }
                    else  
                    {   // se a execução está correta, apenas o valor "4" seria possível neste ponto
                        if (readerState == 4)
                        {
                            this.readerState -= 4;
                        }
                        else
                        {
                            System.out.println ("ERRO: Valor inválido para o 'estadoLeitor' - no momento da alteração.") ;
                            return false;    //Encerra a leitura do arquivo
                        }                        
                    }
                }
                else
                {
                    System.out.println ("ERRO: Caractere invalido para encerramento de linha = " + 
                                        this.textInputBayes.substring(i-4,i+4) +
                                        "\n\tPosicao 'i': " + i ) ;
                    return false;    //Encerra a leitura do arquivo
                }
            }   
            
            //Mostra o resultado final da leitura do arquivo, com as variáveis de entrada já organizadas
            System.out.println(this.hypothesis.showStates() + this.hypothesis.showEvidences());
                        
            //Neste ponto, a leitura do arquivo deve ter sido concluída com sucesso
            System.out.println ("\n\n\t > SUCESSO: Leitura do arquivo de entrada '" + fileName + "' concluída com sucesso.") ;     
            return true;
        }
        else    // Caso o arquivo de entrada esteja em branco, é declarado um erro
        {
            System.out.println ("ERRO: O arquivo de entrada está vazio.");
            return false;            
        }
    }

// ------                                                                     ------ //   
// -------                                                                   ------- //
// --------------- FIM DA SESSAO DE METODOS PARA ENTRADA DE ARQUIVOS --------------- //    
// -----------------------------~~~~~~~~~~~~~~~------------------------------------- //
// -----------------------------~~~~~~~~~~~~~~~------------------------------------- //
//  ~~~xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx~~~  //
// -----------------------------~~~~~~~~~~~~~~~------------------------------------- //
// -----------------------------~~~~~~~~~~~~~~~------------------------------------- //    
// ------ SESSAO DE METODOS PARA CALCULO DE PROBABILIDADES BAYESIANAS (PURAS) ------ //
// ------                                                                     ------ //    
// -------                                                                   ------- //
    
    //Metodo que calcula as probabilidades de todos os estados da hipotese, com o teorema de Bayes puro
    //  > Para todos os estados das evidencias, individualmente
    //  > Após a entrada já ter sido carregada com sucesso
    public void calculateBayes ()
    {
        String out = "\n\n ---> SAIDA DO TEOREMA DE BAYES PURO -> P(Hi|e)\n\n";
        
        for (Evidence e : this.hypothesis.getBayesEvids())
        {
            out += "\tEvidencia: '" + e.getNodeName() + "'\n";
            for (EvidenceState es : e.getEvidenceStates())
            {
                out += "\t\tEstado: '" + es.getNameEvidState() + "'\n\t\t\tP(Hi|e) = ";
                for (int i = 0 ; i < es.getProbs_Evid_given_Hyp().size() ; i++)
                {                   
                    float prob_Ei_Hi = es.getProbs_Evid_given_Hyp().get(i);                // Probabilidade de um estado da Evidencia dado um estado da Hipotese
                    float prob_Ei = es.getProbEvidState() ;                                // Probabilidade de um estado da evidencia
                    float prob_Hi = this.hypothesis.getHypStates().get(i).getProb_Hyp();   // Probababilidade de um estado de Hipotese (a priori)
                    
                    float prob_Hi_Ei = (prob_Ei_Hi * prob_Hi) / prob_Ei ;                  // Probabilidade do estado da hipotese dado o estado da evidencia
                    
                    //  com duas variaveis                    Double prob_Hi_e1 ^ e2 = (prob_E1_Hi * prob_E2_hi * prob_Hi) / prob_(E1^E2) ;
                    // fuzzy  Double prob_Hi_e = (sum pra toda classe de E (prob_E_Hi * uE) * prob_Hi) / prob_E ;
                    out += prob_Hi_Ei + ", ";                    
                }
                out += "\n" ;
            }
             out += "\n" ;
        }        
        System.out.println(out);
    }
    
    //Realiza o calculo das probabilidades com bayes puro, de acordo com especificacoes do usuario
    //  > Usuario escolhe qual estado da hipotese deseja confirmar
    //  > Usuario escolhe quais evidencias deseja considerar, e escolhe 1 estado de cada evidencia
    public void calculateBayesMultiEvid ()
    {       
        //Guarda localmente o estado da hipotese escolhido pelo usuario
        HypothesisState hi_chosen = this.chooseHypState();
        
        int index_hi = this.hypothesis.getStateIndex(hi_chosen);
        
        //Guarda as evidencias apenas com os estados escolhidos pelo usuario
        ArrayList<Evidence> selectedStateEvids = this.chooseEvidsWithStates();
        
        //Guarda as evidencias escolhidas pelo usuario, mas com todos os seus estados
        //ArrayList<Evidence> selectedFullEvids = this.hypothesis.getFullStateEvids(selectedStateEvids);        
        
        // Para 2 evidencias, seria representado assim: 
        //                    P(Hi| e1 ^ e2)
        float result = 0f ;
        
        // Para facilitar o calculo, a equacao foi separada em  duas partes: "upper" e "lower"
        // Exemplo de upper para 2 evidencias: P(Hi) * P(e1|Hi) * P(e2|Hi)
        // Upper se inicializa com o valor "P(Hi)"
        float upper = hi_chosen.getProb_Hyp(); 
        System.out.println ("\n\n\t~~~~~> P(Hi) = " + upper +
                    "\n\t~~~~~> Index P(Hi) = " + index_hi);
        
        // Exemplo de lower para 2 evidencias:
        float lower = 0 ;//           (P(H1) * P(b|H1) * P(e|H1)) + (P(H2) * P(b|H2) * P(e|H2))
        
        for (Evidence evi : selectedStateEvids)
        {
            upper *= evi.getLastState().getProb_Evid_given_Hyp(index_hi);
            System.out.println ("\n\n\t~~~~~> P(e | Hi) " + evi.getLastState().getNameEvidState() +
                    " = " + evi.getLastState().getProb_Evid_given_Hyp(index_hi));
        }
        
        System.out.println ("\n\n\t~~~~~> Upper = " + upper);
        
        for (int i = 0 ; i < this.hypothesis.getNumStates() ; i++)
        {
            float hi = this.hypothesis.getHypState(i).getProb_Hyp() ;
            float aux = hi ;
            for (Evidence evi : selectedStateEvids)
            {
                System.out.println ("\n\n\t>>>>>>>>>> Aux H(" + i + ") = " + aux);
                aux *= evi.getLastState().getProb_Evid_given_Hyp(i);
                System.out.println ("\n\n\t>>>>>>>>>> Aux *= " + aux);
            }
            lower += aux ;
            System.out.println ("\n\n\t\t>>>>>>>>>> Lower += " + aux);
        }
        
        result = upper / lower ;        
        
        String out2 = "\nEstado da Hipotese escolhido: " + hi_chosen.getNameHypState() +
                        "\n\nEvidencias e estados escolhidos: " + selectedStateEvids.isEmpty() + "\n";

        for (Evidence evi : selectedStateEvids)
        {
            out2 += "\n> Evidencia: " + evi.getNodeName() + "\n" + evi.showStates();
        }
        System.out.println (out2);
        
        String out3 = "\n\n ---> SAIDA DO TEOREMA DE BAYES PURO com quantidade variavel de evidencias"
        + " ----> P(Hi|e1 ^ e2... ^ eN) = \n\n" + result;        
        
        System.out.println (out3);
    }
    
    
   
    
    
    
    //Realiza o calculo das probabilidades com bayes puro, de acordo com especificacoes do usuario
    //  > Usuario escolhe qual estado da hipotese deseja confirmar
    //  > Usuario escolhe quais evidencias deseja considerar, e escolhe 1 estado de cada evidencia
    public float calculateBayesMultiHypStates ()
    {
        // Para 2 evidencias, seria representado assim: 
        //                    P(Hi| e1 ^ e2)
        float result = 0f ;
        
        String output = "\n\n---> SAIDA DO TEOREMA DE BAYES PURO com quantidade variavel de evidencias"
            + " ----> P(Hi|e1 ^ e2... ^ eN\n";

        
        //Guarda as evidencias apenas com os estados escolhidos pelo usuario
        ArrayList<Evidence> selectedStateEvids = this.chooseEvidsWithStates();
        
        for (HypothesisState hs : this.hypothesis.getHypStates())
        {
            int index_hi = this.hypothesis.getStateIndex(hs);
            
            // Para facilitar o calculo, a equacao foi separada em  duas partes: "upper" e "lower"
            // Exemplo de upper para 2 evidencias: P(Hi) * P(e1|Hi) * P(e2|Hi)
            // Upper se inicializa com o valor "P(Hi)"
            float upper = hs.getProb_Hyp(); 
            
            System.out.println ("\n\n\t~~ Calc Bayes Multi HIP ~~~> P(Hi) = " + upper +
                        "\n\t~~~~~> Index P(Hi) = " + index_hi);

            // Exemplo de lower para 2 evidencias:
            float lower = 0f ;//           (P(H1) * P(b|H1) * P(e|H1)) + (P(H2) * P(b|H2) * P(e|H2))

            for (Evidence evi : selectedStateEvids)
            {
                upper *= evi.getLastState().getProb_Evid_given_Hyp(index_hi);
                System.out.println ("\n\n\t~~ Calc Bayes Multi HIP 2~~~> P(e | Hi) " + evi.getLastState().getNameEvidState() +
                        " = " + evi.getLastState().getProb_Evid_given_Hyp(index_hi));
            }

//            System.out.println ("\n\n\t~~~~~> Upper = " + upper);

            for (int i = 0 ; i < this.hypothesis.getNumStates() ; i++)
            {
                float hi = this.hypothesis.getHypState(i).getProb_Hyp() ;
                float aux = hi ;
                for (Evidence evi : selectedStateEvids)
                {
//                    System.out.println ("\n\n\t>>>>>>>>>> Aux H(" + i + ") = " + aux);
                    aux *= evi.getLastState().getProb_Evid_given_Hyp(i);
//                    System.out.println ("\n\n\t>>>>>>>>>> Aux *= " + aux);
                }
                lower += aux ;
//                System.out.println ("\n\n\t\t>>>>>>>>>> Lower += " + aux);
            }

            result = upper / lower ;
            
            output += "\n\t> P(Hi | e) - Estado da Hipotese: " + hs.getNameHypState() +
                    "\n\t\t para as evidencias: " ;
            for (Evidence evi : selectedStateEvids)
            {
               output += "(" + evi.getNodeName() + " = "
                       + evi.getLastState().getNameEvidState() + "), ";
            }
            output += "\n\t = " + result + "\n";                       
            
//            String out2 = "\nEstado da Hypothesis escolhido: " + eh.getNameHypState() +
//                        "\n\nEvidencias e estados escolhidos: " + selectedStateEvids.isEmpty() + "\n";
//            
//            for (Evidence evi : selectedStateEvids)
//            {
//                out2 += "\n> Evidence: " + evi.getNodeName() + "\n" + evi.showStates();
//            }
//            System.out.println (out2);
//
//            String out3 = "\n\n ---> SAIDA DO TEOREMA DE BAYES PURO com quantidade variavel de evidencias"
//            + " ----> P(Hi|e1 ^ e2... ^ eN) = \n\n" + resultado;        
//
//            System.out.println (out3);
        }
        System.out.println(output); 
        return result;
    }
    
    
     //Realiza o calculo das probabilidades com bayes puro, de acordo com especificacoes do usuario
    //  > Usuario escolhe qual estado da hipotese deseja confirmar
    //  > Usuario escolhe quais evidencias deseja considerar, e escolhe 1 estado de cada evidencia
    public String calculateBayesMultiHypStatesUI (Hypothesis theHypothesis, ArrayList<Evidence> chosenEvids)
    {
        // Para 2 evidencias, seria representado assim: 
        //                    P(Hi| e1 ^ e2)
        float result = 0f ;
        
//        String output = "\n\n---> SAIDA DO TEOREMA DE BAYES PURO com quantidade variavel de evidencias"
//            + " ----> P(Hi|e1 ^ e2... ^ eN\n";

        String output = "\n\nResultado do Teorema de Bayes Puro:\n\n";
        
        Hypothesis theHyp = theHypothesis;
        
        //Guarda as evidencias apenas com os estados escolhidos pelo usuario
        ArrayList<Evidence> selectedStateEvids = chosenEvids;
        
        for (HypothesisState hs : theHyp.getHypStates())
        {
            int index_hi = theHyp.getStateIndex(hs);
            
            // Para facilitar o calculo, a equacao foi separada em  duas partes: "upper" e "lower"
            // Exemplo de upper para 2 evidencias: P(Hi) * P(e1|Hi) * P(e2|Hi)
            // Upper se inicializa com o valor "P(Hi)"
            float upper = hs.getProb_Hyp(); 
            
            System.out.println ("\n\n\t~~ Calc Bayes Multi HIP ~~~> P(Hi) = " + upper +
                        "\n\t~~~~~> Index P(Hi) = " + index_hi);

            // Exemplo de lower para 2 evidencias:
            float lower = 0f ;//           (P(H1) * P(b|H1) * P(e|H1)) + (P(H2) * P(b|H2) * P(e|H2))

            for (Evidence evi : selectedStateEvids)
            {
                upper *= evi.getLastState().getProb_Evid_given_Hyp(index_hi);
                System.out.println ("\n\n\t~~ Calc Bayes Multi HIP 2~~~> P(e | Hi) " + evi.getLastState().getNameEvidState() +
                        " = " + evi.getLastState().getProb_Evid_given_Hyp(index_hi));
            }

//            System.out.println ("\n\n\t~~~~~> Upper = " + upper);

            for (int i = 0 ; i < theHyp.getNumStates() ; i++)
            {
                float hi = theHyp.getHypState(i).getProb_Hyp() ;
                float aux = hi ;
                for (Evidence evi : selectedStateEvids)
                {
//                    System.out.println ("\n\n\t>>>>>>>>>> Aux H(" + i + ") = " + aux);
                    aux *= evi.getLastState().getProb_Evid_given_Hyp(i);
//                    System.out.println ("\n\n\t>>>>>>>>>> Aux *= " + aux);
                }
                lower += aux ;
//                System.out.println ("\n\n\t\t>>>>>>>>>> Lower += " + aux);
            }

            result = upper / lower ;
            
            output += "\n\t> P(Hi | e) - Estado da Hipotese: " + hs.getNameHypState() +
                    "\n\t\t para as evidencias: " ;
            for (Evidence evi : selectedStateEvids)
            {
               output += "(" + evi.getNodeName() + " = "
                       + evi.getLastState().getNameEvidState() + "), ";
            }
            output += "\n\t = " + result + "\n";                       
            
//            String out2 = "\nEstado da Hypothesis escolhido: " + eh.getNameHypState() +
//                        "\n\nEvidencias e estados escolhidos: " + selectedStateEvids.isEmpty() + "\n";
//            
//            for (Evidence evi : selectedStateEvids)
//            {
//                out2 += "\n> Evidence: " + evi.getNodeName() + "\n" + evi.showStates();
//            }
//            System.out.println (out2);
//
//            String out3 = "\n\n ---> SAIDA DO TEOREMA DE BAYES PURO com quantidade variavel de evidencias"
//            + " ----> P(Hi|e1 ^ e2... ^ eN) = \n\n" + resultado;        
//
//            System.out.println (out3);
        }
        System.out.println(output); 
        return output;
    }
    
    
    //Pede-se ao usuario que defina qual estado da hipotese deseja-se confirmar
    public HypothesisState chooseHypState ()
    {
        String namesHypStates = "\n\n" ;
        int count = 0 ;
        for (HypothesisState h : this.hypothesis.getHypStates())
        {
            namesHypStates += "\t" + count + " - " + h.getNameHypState() + "\n";
            count++;
        }
        namesHypStates += "\n";
        
//        System.out.println ("\nEstados da hipotese: " + estadosHip + 
//                "Digite o numero correspondente ao estado escolhido: ");
        
        // pede ao usuario para escolher o estado da hipotese a ser usado
        String in = JOptionPane.showInputDialog("\nEstados da hipotese: " + namesHypStates + 
                "Digite o numero correspondente ao estado escolhido: ", "0");
        
        System.out.println("\nEstados da hipotese: " + namesHypStates + 
                "Digite o numero correspondente ao estado escolhido: ");
        
        //vai guardar o estado escolhido
        int index = -1 ;
        
        try
        {
            index = Integer.parseInt(in) ;
        }
        catch (Exception e)
        {
            System.out.println (e.getMessage());
        }
        
        HypothesisState hypState = this.hypothesis.getHypStates().get(index);
        
        System.out.println ("\n\nEstado escolhido da hipotese: " + hypState.getNameHypState());
        
        return hypState ;        
    }
    
    
    //Pede-se ao usuario que defina quais evidencias deseja confirmar
    //Retorna um conjunto apenas com as evidencias escolhidas
    public ArrayList<Evidence> chooseHypEvidences ()
    {                 
        String namesHypEvids = "\n\n" ;
        
        int count2 = 0 ;
        
        for (Evidence e : this.hypothesis.getBayesEvids())
        {
            namesHypEvids += count2 + " - " + e.getNodeName() + "\n";
            count2++;
        }
        namesHypEvids += "\n";
         
        // pede ao usuario para escolher as evidencias da hipotese a serem usadas
        String in2 = JOptionPane.showInputDialog("\nEvidencias da hipotese: " + namesHypEvids + 
                "Digite os numeros correspondentes às evidencias escolhidas "+
                        "\n(Separados por virgulas e em ordem): ", "0,1");
        
        //vai guardar os ponteiros para as evidencias escolhidas
        ArrayList<Integer> indexEvids = new ArrayList<Integer>();
                       
        try
        {
            int index2 = 0 ;
            String read = "" ;
            for (int i = 0 ; i < in2.length() ; i++)
            {                
                if (in2.charAt(i) != ',')
                {
                    read += in2.charAt(i) ;
//                    System.out.println ( "Lido: " + read);
                }
                else
                {
                    index2 = Integer.parseInt(read) ;
                    indexEvids.add(index2);
                    read = "" ;                    
//                    System.out.println ( "Salvo: " + indexEvids.get(indexEvids.size()-1));
                }
                if ( i == in2.length()-1)
                {
                    index2 = Integer.parseInt(read) ;
                    indexEvids.add(index2);
                    read = "" ;                    
//                    System.out.println ( "Salvo: " + indexEvids.get(indexEvids.size()-1));
                }                
            }
            
            String out = "\n\nIndices das evidencias: \n";
            
            ArrayList<Evidence> hypEvids = new ArrayList<>();
            
            for (Integer integer : indexEvids)
            {
                hypEvids.add(this.hypothesis.getBayesEvids().get(integer));
                String nameEvid = this.hypothesis.getBayesEvids().get(integer).getNodeName();
                out += "\t> Indice: " + integer + " > Evidencia: " + nameEvid + "\n" ;
            }
            
//            System.out.println (out);
            
            return hypEvids ;            
        }
        catch (Exception e)
        {
            System.out.println (e.getMessage());
        }
        return null ;
    }
    
    //Pede-se ao usuario que defina quais estados das evidencias deseja confirmar
    //Retorna o conjunto de evidencias escolhidas, as quais contem apenas os estados desejados
    public ArrayList<Evidence> chooseEvidsWithStates ()
    {
        String namesEvidsWithStates = "\n" ;       
        
        int count3 ;
        
        ArrayList<Evidence> chosenEvids = this.chooseHypEvidences();
        
        for (Evidence e : chosenEvids)
        {
            namesEvidsWithStates += "\n\tEvidencia: " + e.getNodeName() + " \n- Estados: \n";
            
            count3 = 0 ;
            for (EvidenceState est : e.getEvidenceStates())
            {
                namesEvidsWithStates += "\t\t " + count3 + " - " + est.getNameEvidState() + "\n" ;
                count3++ ;
            }            
        }
        namesEvidsWithStates += "\n";
         
        // pede ao usuario para escolher as evidencias da hipotese a serem usadas
        String in3 = JOptionPane.showInputDialog("\nEstados das Evidencias: " + namesEvidsWithStates + 
                "Escolha um estado para cada evidencia, digitando seu numero correspondente."+
                        "\n(Separados por virgulas e em ordem): ", "0,0");
        
        //vai guardar os ponteiros para as evidencias escolhidas
        ArrayList<Integer> indexStates = new ArrayList<Integer>();
                       
        try
        {
            int index3 = 0 ;
            String read = "" ;
            for (int i = 0 ; i < in3.length() ; i++)
            {                
                if (in3.charAt(i) != ',')
                {
                    read += in3.charAt(i) ;                    
                }
                else
                {
                    index3 = Integer.parseInt(read) ;
                    indexStates.add(index3);
                    read = "" ;
                }
                if ( i == in3.length()-1)
                {
                    index3 = Integer.parseInt(read) ;
                    indexStates.add(index3);
                    read = "" ; 
                }                
            }            
                                    
            ArrayList<Evidence> evidsWithStates = new ArrayList<>();            
            
            //prepara as evidencias escolhidas para que cada uma contenha apenas o estado desejado
            for (int i = 0 ; i < indexStates.size() ; i++)
            {
                Evidence evid = chosenEvids.get(i) ;
                int index = indexStates.get(i);
                EvidenceState chosenState = evid.getEvidenceStates().get(index);
                ArrayList<EvidenceState> clean = new ArrayList<EvidenceState>();
                clean.add(chosenState);
                evid.setEvidenceStates(clean);
                evidsWithStates.add(evid);
//                System.out.println ( "\nLimpou estados?: " + evid.getEstadosEvidencia().isEmpty());  
            }
            
//            String out = "\nEvidencias e estados escolhidos: \n";
//            
//            for (Evidence evi : evidsComEstados)
//            {
//                out += "\n Evidence: " + evi.getNodeName() + "\n" + evi.showStates();
//            }
//
//            System.out.println (out);
//            
//            System.out.println ( "\n\t~~~~Evidencias com estados = OK");;
            
            return evidsWithStates ;            
        }
        catch (Exception e)
        {
            System.out.println ("Erro: " + e.getCause());
        }
        return null ;
    }
   

    //Inicializa o "fuzzyHandler" a partir de um arquivo
    public void readFuzzyFile (String fileName)
    {
        this.fuzzyHandler = FIS.load(fileName, true);  
        
        if (this.fuzzyHandler == null)
        {
            System.err.println("Nao foi possivel carregar o arquivo de entrada Fuzzy: '" + fileName + "'");
            return;
        }
    }
    
    //Inicializa o "fuzzyHandler" a partir de um string (inicialmente fornecido através da interface gráfica)
    public void readFuzzyFromString(String fuzzyFclFormatInput)
    {
        try
        {            
            this.fuzzyHandler = FIS.createFromString(fuzzyFclFormatInput, true) ;

//            if (this.fuzzyHandler == null)
//            {
//                System.err.println("Nao foi possivel carregar o arquivo de entrada Fuzzy: '" + fileName + "'");
//                return;
//            }
        }
        catch (RecognitionException rException)
        {
            System.err.println("\n %% ERRO ~ Nao foi possivel carregar o input Fuzzy: " + rException.getMessage());
        }        
    }
    
    // Define, para a evidencia "evidName" o valor medido "measurement"
    public void setMeasurement (String evidName, float measurement)
    {   
        this.fuzzyHandler.setVariable (evidName, measurement);     
    }
    
    //Realiza os calculos fuzzy com base nos valores de entrada fornecidos
    public void fuzzyEvaluation ()
    {
        this.fuzzyHandler.evaluate();
    }
      
    //Mostra os gráficos fuzzy
    public void showFuzzyGraphics()
    {
        JFuzzyChart.get().chart(this.fuzzyHandler);
    }
    
    
    // Calcula a pertinencia de uma medida para cada estado de uma evidencia (estados da evidencia = conjuntos fuzzy)
    public void calcMembership (String evidName, float measurement)
    {
        this.setMeasurement(evidName, measurement);
        this.fuzzyEvaluation();        
    }    
    
    // Pede ao usuário que escolha as evidências às quais deseja adicionar medidas
    public ArrayList<Evidence> chooseFuzzyEvidToCalc()
    {      
        ArrayList<Evidence> chosenEvids = this.chooseHypEvidences();
        //System.out.println(evidsEscolhidas);
        //String nomesEvids = "\n" ;  
        ArrayList<String> evidsNamesList = new ArrayList<>();    
        //int cont3 = 0 ;
        //monta as opcoes para usuario
        for (Evidence e : chosenEvids)
        {
           String name = e.getNodeName();
           evidsNamesList.add(name);
        }
        System.out.println("Evidências escolhidas: "+evidsNamesList);
        return chosenEvids;        
    }
    
    // Pede ao usuario que forneca as medidas para as evidencias escolhidas
    public ArrayList<Integer> defineMeasures(ArrayList<Evidence> chosenEvids)
    {
        ArrayList<String> evidsNamesList = new ArrayList<>();   
        //monta a lista com nomes de todas as evidencias escolhidas
        for (Evidence e : chosenEvids)
        {
            evidsNamesList.add(e.getNodeName());
        }
        //int cont3 = 0 ;
        //monta as opcoes para usuario
        /*
        for (Evidence e : evidsEscolhidas)
        {
           String nome = e.getEstadosEvidencia().get(cont3).getNameEvidState();
           listanomesEvids.add(nome);
        }
*/
        // pede ao usuario para definir as medidas de cada evidencia
        String measures = JOptionPane.showInputDialog(evidsNamesList + 
                "\nDigite o numero correspondente à medida que deseja adicionar à cada evidência","10,40");
       
       // System.out.println("tamanho do array das evidencias escolhidas: " +listanomesEvids.size());
        ArrayList<Integer> measuresList = new ArrayList();
        int meas ;
        String m = "" ;
        for (int i = 0 ; i < measures.length() ; i++)
        {                
            if (measures.charAt(i) != ',')
            {
                m  += measures.charAt(i) ;  
            }

            else
            {
                meas = (int) Float.parseFloat(m) ;
                measuresList.add(meas);
                m = "" ;                      
            }
            if ( i == measures.length()-1)
            {
                meas = (int) Float.parseFloat(m) ;
                measuresList.add(meas);
                m = "" ;       
            }                
        }
        System.out.println("Lista de medidas:\t "+measuresList);
        return measuresList;
    }
    
    // Calcula pertinência das medicoes para todas as evidencias escolhidas
    public ArrayList<Float> calcPertinenceEvids(ArrayList<Evidence> chosenEvids)
    {
        //ArrayList<Evidencia> evidsEscolhidas = new ArrayList();
        //evidsEscolhidas = this.chooseFuzzyEvidToCalc();
        ArrayList<Integer> measuresList = new ArrayList<Integer>();
        measuresList = this.defineMeasures(chosenEvids);
        ArrayList<Float> membershipList = new ArrayList<Float>();
         ArrayList<String> namesEvidsList = new ArrayList<>();    
        //monta array de nomes das evidencias
        for (Evidence e : chosenEvids)
        {
           String name = e.getNodeName();
           namesEvidsList.add(name);          
        }
                
            for(int i=0; i<namesEvidsList.size(); i++)
            {
                String evid = namesEvidsList.get(i);
                
                int me = measuresList.get(i);
               
                this.calcMembership(evid, me);
                    for(int j=0; j<chosenEvids.get(i).getEvidenceStates().size();j++)
                    {
                        float member = (float) fuzzyHandler.getVariable(namesEvidsList.get(i)).getMembership(chosenEvids.get(i).getEvidenceStates().get(j).getNameEvidState());
                        
                        // TESTE
                        System.out.println("~Método > calcPertinenceEvids(ArrayList<Evidence> chosenEvids): " + namesEvidsList.get(i) + " " + 
                                chosenEvids.get(i).getEvidenceStates().get(j).getNameEvidState() + ": " + member);
                        
                        membershipList.add(member);   
                    }              
            }         
            //JFuzzyChart.get().chart(fuzzyHandler);
            this.showFuzzyGraphics();     // MOSTRAR GRAFICOS = HABILITAR ESSA LINHA AQUIS
            //TESTE
            System.out.println( "~~ Lista de Pertinências: " + membershipList);
            return membershipList;       
    }
    
    // Calcula pertinência das medicoes para todas as evidencias escolhidas
    public ArrayList<Float> calcPertinenceEvidsWithMeasures(ArrayList<Evidence> chosenEvids)
    {
        //ArrayList<Evidencia> evidsEscolhidas = new ArrayList();
        //evidsEscolhidas = this.chooseFuzzyEvidToCalc();
//        ArrayList<Integer> measuresList = new ArrayList<Integer>();
//        measuresList = this.defineMeasures(chosenEvids);
        ArrayList<Float> membershipList = new ArrayList<Float>();
//         ArrayList<String> namesEvidsList = new ArrayList<>();
        //monta array de nomes das evidencias
//        for (Evidence e : chosenEvids)
//        {
//           String name = e.getNodeName();
//           namesEvidsList.add(name);          
//        }

        for(Evidence evid : chosenEvids)
        {
//            String evid = namesEvidsList.get(i);

//            int me = measuresList.get(i);

            this.calcMembership(evid.getNodeName(), evid.getMeasure());
            for( EvidenceState state : evid.getEvidenceStates())
            {
                float member = (float) fuzzyHandler.getVariable(evid.getNodeName()).getMembership(state.getNameEvidState());
                
                // TESTE
                System.out.println("~~ Método ~> calcPertinenceEvidsWithMeasures(ArrayList<Evidence> chosenEvids): " +
                        evid.getNodeName() + " " + state.getNameEvidState() + ": " + member);
                membershipList.add(member);   
            }              
        }         
        //JFuzzyChart.get().chart(fuzzyHandler);
        this.showFuzzyGraphics();     // MOSTRAR GRAFICOS = HABILITAR ESSA LINHA AQUI
        
        // TESTE
        System.out.println( "~~ Método ~> calcPertinenceEvidsWithMeasures(ArrayList<Evidence> chosenEvids):" + membershipList);
        return membershipList;       
    }
    
    
    
    // Realiza o calculo final da rede bayesiana com as pertinencias calculadas a partir das medidas inseridas
    public void calcFuzzyBayes()
    {
       //Guarda as evidencias apenas com os estados escolhidos pelo usuario
       ArrayList<Evidence> selectedEvids = this.chooseFuzzyEvidToCalc();
       ArrayList<Float> memberships = this.calcPertinenceEvids(selectedEvids);

       float result = 0f ;

//        String saida = "\n\n---> SAIDA HIBRIDA FUZZY BAYES";        

       float lower = 0f;
       float upper = 0f;

       ArrayList<Float> upsList = new ArrayList<Float>();

       for (HypothesisState hs : this.hypothesis.getHypStates())
       {
           int index_hi = this.hypothesis.getStateIndex(hs);
           // Upper se inicializa com o valor "P(Hi)"
           upper = this.hypothesis.getHypStates().get(index_hi).getProb_Hyp(); 
           float memberSum; 

           //double lower = 0 ;            
           int countMember = 0;

           for (int i=0; i<selectedEvids.size(); i++)
           {
               memberSum = 0;

               for(int j=0 ; j <selectedEvids.get(i).getEvidenceStates().size() ; j++)
               {
                   System.out.println("Hipotese: "+this.hypothesis.getHypStates().get(index_hi).getNameHypState());
                   System.out.println("\tEvidencia:" +selectedEvids.get(i).getNodeName());
                   System.out.println("\tEstado evid: "+selectedEvids.get(i).getEvidenceStates().get(j).getNameEvidState());
                   System.out.println("\tProb Evid dada Hip: "+selectedEvids.get(i).getEvidenceStates().get(j).getProb_Evid_given_Hyp(index_hi));
                   System.out.println("\tPertinencia: "+memberships.get(countMember));
                   memberSum += selectedEvids.get(i).getEvidenceStates().get(j).getProb_Evid_given_Hyp(index_hi)*memberships.get(countMember);

                   countMember++;
               }
               upper = upper*memberSum;
               System.out.println("Upper temp: " +upper);
               System.out.println ("\tSOMA PERTINENCIAS PARA EVIDENCIA: "+memberSum);                   

               }
               upsList.add(upper);
               lower += upper;
               //System.out.println("Lower parcial: "+lower); 
               System.out.println("upper: " + upper);
               System.out.println("________________________");                           
       }
       System.out.println("\tArray de ups: "+upsList);

       for(int i=0; i < upsList.size(); i++)
       {
           result = upsList.get(i) / lower;
           System.out.println("\tResultado fuzzificado para " + this.hypothesis.getHypStates().get(i).getNameHypState() + ": " + result);
       }                       
    }   
    
     // Realiza o calculo final da rede bayesiana com as pertinencias calculadas a partir das medidas inseridas
    public String calcFuzzyBayesUI(Hypothesis theHypothesis, ArrayList<Evidence> evidsWithMeasures)
    {
        String output = "" ;
        
        this.hypothesis = theHypothesis; 
       
        //Guarda as evidencias apenas com os estados escolhidos pelo usuario
        ArrayList<Evidence> measuredEvidences = evidsWithMeasures;

        ArrayList<Float> memberships = this.calcPertinenceEvidsWithMeasures(evidsWithMeasures);

        float result = 0f ;

    //        String saida = "\n\n---> SAIDA HIBRIDA FUZZY BAYES";        

        float lower = 0f;
        float upper = 0f;

        ArrayList<Float> upsList = new ArrayList<Float>();

        // TESTE
        System.out.println("~~> InputHandler --> calcFuzzyBayesUI()=1 ~> quantidade estados da hyp: " + this.hypothesis.getHypStates().size());
        for (HypothesisState hs : this.hypothesis.getHypStates())
        {
            int index_hi = this.hypothesis.getStateIndex(hs);
            // Upper se inicializa com o valor "P(Hi)"
            upper = this.hypothesis.getHypStates().get(index_hi).getProb_Hyp(); 
            float memberSum; 

            //double lower = 0 ;            
            int countMember = 0;

            for ( Evidence evid : measuredEvidences )
            {
                memberSum = 0;

                for( EvidenceState state : evid.getEvidenceStates() )
                {
                    String fuzzyfying = "\n~~FUZZYFYING: \n\tHipotese: " + this.hypothesis.getHypStates().get(index_hi).getNameHypState() +
                            "\n\t\tEvidencia:" + evid.getNodeName() + " ~ Medição: " + evid.getMeasure() +
                            "\n\t\tEstado evid: " + state.getNameEvidState() +
                            "\n\t\tProb Evid dada Hip: " + state.getProb_Evid_given_Hyp(index_hi) +
                            "\n\t\tPertinencia: " + memberships.get(countMember);

                    // TESTE
                    System.out.println(fuzzyfying);

                    memberSum += state.getProb_Evid_given_Hyp(index_hi)*memberships.get(countMember);

                    countMember++;
                }
                upper = upper*memberSum;

                // TESTE
                System.out.println("Upper temp: " +upper + "\n\t SOMA PERTINENCIAS PARA EVIDENCIA: "+memberSum);                   

                }
                upsList.add(upper);
                lower += upper;
                //System.out.println("Lower parcial: "+lower);

                // TESTE
                System.out.println("upper: " + upper + "\n________________________");                           
        }
        // TESTE
        System.out.println("\t~~> InputHandler -- calcFuzzyBayesUI()=2 ~> Array de ups: "+upsList);

        for(int i=0; i < upsList.size(); i++)
        {
            result = upsList.get(i) / lower;
            output+= "\n\tResultado fuzzificado para " + this.hypothesis.getHypStates().get(i).getNameHypState() + ": " + result;
        }
        return output ;       
    }
}


//    
//   fuzzbay    fuzzb
//           bayfuzz
//   bayzzy
//buzzy
//bazzy 
//fuzzyes
//fayzzyy
//baeyfu
//fayes
//
//BAYES
//FUZZY
        


    


