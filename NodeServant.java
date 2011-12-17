import Eleccion.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.*;
import java.util.Properties;

/**
 *
 * @author Miguel Alejandro
 */
public class NodeServant extends NodePOA {
    private Vector<Node> clients = new Vector<Node>();
	private Node[] objArray;
    private int uid;
	private int l, lmax, vencedor, nresp, pid;
	private String estado;
	private boolean answer;
	private boolean respOK;
	private int[] num = new int[2];
	
    public NodeServant(int uid){
		this.uid = uid;
		this.estado = "no_implicado";
		this.objArray=new Node[2];
    }

    public int getLeader(){
		return this.vencedor;
    }
	
    public void election() {
		System.out.println("Proceso de eleccion iniciado");
		this.estado = "candidato";
		System.out.println("Estado: " + this.estado);
		lmax = 1;
		while (this.estado.equals("candidato")) {
			nresp = 0;
			respOK = true;
			System.out.println("Mandando mensaje de eleccion "+uid);
			sendmessage("candidatura:"+uid+":"+0+":"+lmax+":"+uid);
			while (nresp !=2 && this.estado.equals("candidato")) {
			} 
			if(!respOK){
				this.estado = "derrotado";
			}
			lmax = lmax *2;
		}
		//System.out.println("saliiiii");
    }
	
	public void reply(String msg) {
		String[] temp;
		temp = msg.split(":");
		//System.out.println("arr repply a : "+ num[0] + " arr: "+ num[1]);
			int x =  Integer.parseInt(temp[3]);
			if (x == num[0]) {
				//System.out.println("soy: " + uid +" mensaje reply  de "+ x  + "va para "+ num[0] );
				Node ms = objArray[0];
				ms.message(temp[0]+":"+temp[1]+":"+temp[2]+":"+uid);
				//ms.message(msg);
			} else if (x == num[1]) {
				//System.out.println("soy: " + uid +" mensaje reply  de "+ x  + "va para "+ num[1] );
				Node ms = objArray[1];
				ms.message(temp[0]+":"+temp[1]+":"+temp[2]+":"+uid);
				//ms.message(msg);
			} else {
				System.out.println("NOO entroo a REPLY era para: " + x + " me lo mando: "+ temp[3]);
			}
	}

	public void relay(String msg) {
		String[] temp;
		temp = msg.split(":");
		//System.out.println("arr: "+ num[0] + " arr: "+ num[1]);
		if( temp[0].equals("candidatura") ){
			int x = Integer.parseInt(temp[4]);
			if (x == num[0]) {
				//System.out.println("soy y: " + uid +" mensaje relay cand  de "+ x  + "va para "+ num[1] );
				Node ms = objArray[1];
				ms.message(temp[0]+":"+temp[1]+":"+temp[2]+":"+temp[3]+":"+uid);
				//ms.message(msg);
			} else if (x == num[1]) {
				//System.out.println("soy y: " + uid +" mensaje relay cand  de "+ x  + "va para "+ num[0] );
				Node ms = objArray[0];
				ms.message(temp[0]+":"+temp[1]+":"+temp[2]+":"+temp[3]+":"+uid);
				//ms.message(msg);
			} else{
				System.out.println("NO entro!!!!!!!!!! debia ir a " +temp[4] + "candidatura " +x);
			}			
		} else if (temp[0].equals("terminado") ){
			int x = Integer.parseInt(temp[2]);
			if (x == num[0]) {
				//System.out.println("soy y: " + uid +" mensaje relay term  de "+ x  + "va para "+ num[1] );
				Node ms = objArray[1];
				ms.message(temp[0]+":"+temp[1]+":"+uid);
				//ms.message(msg);
			} else if (x == num[1]) {
				//System.out.println("soy y: " + uid +" mensaje relay term de "+ x  + "va para "+ num[0] );
				Node ms = objArray[0];
				ms.message(temp[0]+":"+temp[1]+":"+uid);
				//ms.message(msg);
			} else{
				System.out.println("NO entro!!!!!!!!!! terminado");
			}		
		} else if (temp[0].equals("respuesta")) {		
			int x = Integer.parseInt(temp[3]);
			//System.out.println("arr: "+ num[0] + " arr: "+ num[1]+" x: "+ x );
			if (x == num[0]) {
				//System.out.println("soy yyyy: " + uid +" mensaje relay resp  de "+ x  + "va para "+ num[1] );
				Node ms = objArray[1];
				ms.message(temp[0]+":"+temp[1]+":"+temp[2]+":"+uid);
				//ms.message(msg);
			} else if (x == num[1]) {
				//System.out.println("soy yyyy: " + uid +" mensaje relay resp  de "+ x  + "va para "+ num[0] );
				Node ms = objArray[0];
				ms.message(temp[0]+":"+temp[1]+":"+temp[2]+":"+uid);
				//ms.message(msg);
			} else{
				System.out.println("NO entro!!!!!!!!!! yyyy");
			}
		}	
	}
	
    public void register(Node nodo, int id, int lado) {
		//clients.addElement(nodo);
		objArray[lado] = nodo;
		num[lado] = id;
		//System.out.println("arr: "+ num[0] + " arr: "+ num[1]);
		//System.out.println("tam: " + clients.size());
    }
	
	public void removeNode(Node nodo) {
		clients.remove(nodo);
    }

    public void sendmessage(String msg) {
		//Iterator it = clients.iterator();
		//while (it.hasNext()) {
		int i=0;
		int n = objArray.length;
		for(i =0; i < n;i++){
			Node ms =  objArray[i];
			//System.out.println("Mando candidato a " + i);
			ms.message(msg);
		}
    }
	
	public void message(String msg) {
		String[] temp;
		/* delimiter */
		String delimiter = ":";
		/* given string will be split by the argument delimiter provided. */
		temp = msg.split(delimiter);
		if( temp[0].equals("candidatura") ){
				pid = Integer.parseInt(temp[1]);
				System.out.println("Se recibe mensaje en "+uid+" de candidatura pid= "+pid);
				//int l = Integer.parseInt(temp[2]);
				//int lmax = Integer.parseInt(temp[3]);
				if( pid > uid) {
					this.estado = "derrotado";
					System.out.println("Estado: " + this.estado);
					l = Integer.parseInt(temp[2]) + 1;
					lmax = Integer.parseInt(temp[3]);
					if( l < lmax) {
						//ret
						//if (temp.length == 5){
						//	System.out.println("Mandando mensaje relay cand "+pid+" mandado de "+temp[4]);
						//	relay("candidatura:"+pid+":"+l+":"+lmax+":"+uid+":"+temp[4]);
						//} else {
							System.out.println("Mandando mensaje relay cand "+pid);
							relay("candidatura:"+pid+":"+l+":"+lmax+":"+temp[4]);
						//}
					} else {
						//resp
						//if (temp.length == 5){
							//System.out.println("Mandando mensaje de respuesta a "+temp[4]);
							//reply("respuesta:"+"true:"+pid+":"+temp[4]);
						//} else {
							System.out.println("Mandando mensaje de respuesta a "+pid);
							reply("respuesta:"+"true:"+pid+":"+temp[4]);
						//}
					}
				} else if (pid < uid) {
					//resp
					System.out.println("Mandando mensaje de respuesta false a "+pid);
					reply("respuesta:"+"false:"+pid+":"+temp[4]);
					if( this.estado.equals("no_implicado") ){
						election();
					}
				} else if (pid == uid) {
					vencedor = uid;
					this.estado = "elegido";
					System.out.println("Estado: " + this.estado);
					//ret
					//System.out.println("estado del proceso : "+ estado);
					System.out.println("Mandando mensaje relay term "+pid);
					relay("terminado:"+pid+":"+temp[4]);
				}
		} else if (temp[0].equals("terminado") ){
			pid = Integer.parseInt(temp[1]);
			System.out.println("Se recibe mensaje en "+uid+" de term pid= "+pid);
			if(vencedor != pid ){
				vencedor = pid;
				System.out.println("Mandando mensaje relay term final "+pid);
				//relay("terminado:"+pid+":"+uid);
				relay(msg);
				this.estado = "no_implicado";
				System.out.println("Estado: " + this.estado);
			}
		} else if (temp[0].equals("respuesta")) {
			pid = Integer.parseInt(temp[2]);
			System.out.println("Se recibe mensaje en "+uid+" de respuesta pid= "+pid);
			answer = Boolean.valueOf(temp[1]);
			if( pid == uid ){
				nresp = nresp + 1;
				respOK = respOK & answer;
				//System.out.println("Entro! a respuesta, nresp "+ nresp);
			} else {
				System.out.println("Mandando mensaje relay respuesta "+pid);
				//relay("respuesta:"+temp[1]+":"+pid+":"+uid);
				relay(msg);
			}
		}
	}
}