package UVS_Fuzzy;

import Components.PetriNet;
import Components.PetriNetWindow;

public class RunAllUVS {

	public static void main(String[] args) {
		System.out.println("Starting Urban Vehicle Flow Control System (UVS Fuzzy)...");

		PetriNet intersectionNet = Intersection.createPetriNet();
		PetriNet lane1Net = Lane1.createPetriNet();
		PetriNet lane2Net = Lane2.createPetriNet();
		PetriNet lane3Net = Lane3.createPetriNet();
		PetriNet lane4Net = Lane4.createPetriNet();
		PetriNet controllerNet = Controller.createPetriNet();

		PetriNetWindow frameIntersection = new PetriNetWindow(false);
		frameIntersection.petriNet = intersectionNet;
		frameIntersection.setTitle("Intersection - Port 1080");
		frameIntersection.setLocation(50, 50);
		frameIntersection.setVisible(true);

		PetriNetWindow frameLane1 = new PetriNetWindow(false);
		frameLane1.petriNet = lane1Net;
		frameLane1.setTitle("Lane 1 - Port 1081");
		frameLane1.setLocation(700, 50);
		frameLane1.setVisible(true);

		PetriNetWindow frameLane2 = new PetriNetWindow(false);
		frameLane2.petriNet = lane2Net;
		frameLane2.setTitle("Lane 2 - Port 1082");
		frameLane2.setLocation(50, 450);
		frameLane2.setVisible(true);

		PetriNetWindow frameLane3 = new PetriNetWindow(false);
		frameLane3.petriNet = lane3Net;
		frameLane3.setTitle("Lane 3 - Port 1083");
		frameLane3.setLocation(700, 450);
		frameLane3.setVisible(true);

		PetriNetWindow frameLane4 = new PetriNetWindow(false);
		frameLane4.petriNet = lane4Net;
		frameLane4.setTitle("Lane 4 - Port 1084");
		frameLane4.setLocation(1350, 450);
		frameLane4.setVisible(true);

		PetriNetWindow frameController = new PetriNetWindow(false);
		frameController.petriNet = controllerNet;
		frameController.setTitle("Controller - Port 1085");
		frameController.setLocation(1350, 50);
		frameController.setVisible(true);

		System.out.println("All UVS Fuzzy windows opened.");
	}
}
