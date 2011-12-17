import Eleccion.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.io.*;

/**
 *
 * @author Miguel Alejandro
 */
public class NodeClient {

public static void main (String args []) {
 try{
    //crea e inicializa el orb
    ORB orb = ORB.init (args, null);
    org.omg.CORBA.Object objRef = orb.resolve_initial_references ("NameService");
    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	NameComponent nc1 = new NameComponent("node0", "");
    NameComponent path[] = {nc1};
	Node n1 = NodeHelper.narrow(ncRef.resolve(path));

	n1.election();
	int leader = n1.getLeader();
	System.out.println("El lider fue: "+leader);

	
    }catch (Exception e) {
    System.out.println("ERROR:"+ e);
    e.printStackTrace(System.out);
    }
}
}

