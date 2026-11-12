package UVS_Fuzzy;

import java.util.ArrayList;
import Components.Activation;
import Components.Condition;
import Components.GuardMapping;
import Components.PetriNet;
import Components.PetriNetWindow;
import Components.PetriTransition;
import DataObjects.DataFuzzy;
import DataObjects.DataTransfer;
import DataOnly.FLRS;
import DataOnly.FV;
import DataOnly.Fuzzy;
import DataOnly.PlaceNameWithWeight;
import DataOnly.TransferOperation;
import Enumerations.FZ;
import Enumerations.LogicConnector;
import Enumerations.TransitionCondition;
import Enumerations.TransitionOperation;

public class Lane2 {

	public static PetriNet createPetriNet() {
		PetriNet pn = new PetriNet();
		pn.PetriNetName = "Lane 2";
		pn.NetworkPort = 1082;

		// --------------------------------------------------------------------
		// ----------------------- FLRS Move (1x1) ----------------------------
		// --------------------------------------------------------------------
		FLRS flrsMove = new FLRS(
				new FV(FZ.NL),
				new FV(FZ.NM),
				new FV(FZ.ZR),
				new FV(FZ.PM),
				new FV(FZ.PL)
		);

		// --------------------------------------------------------------------
		// ----------------------- Constant Places ----------------------------
		// --------------------------------------------------------------------
		DataFuzzy green = new DataFuzzy();
		green.SetName("green");
		green.SetValue(new Fuzzy(0.0F));
		green.Printable = false;
		pn.ConstantPlaceList.add(green);

		// --------------------------------------------------------------------
		// -------------------- A: Entrance Lane Model ------------------------
		// --------------------------------------------------------------------
		DataFuzzy pIn = new DataFuzzy();
		pIn.SetName("P_in");
		pIn.SetValue(new Fuzzy(0.3F));
		pn.PlaceList.add(pIn);

		DataFuzzy lane1 = new DataFuzzy();
		lane1.SetName("lane1");
		pn.PlaceList.add(lane1);

		DataFuzzy lane2 = new DataFuzzy();
		lane2.SetName("lane2");
		pn.PlaceList.add(lane2);

		DataFuzzy pTL = new DataFuzzy();
		pTL.SetName("P_TL");
		pn.PlaceList.add(pTL);

		// P_out to Intersection: Port of Intersection (1080), place P_i2
		DataTransfer port = new DataTransfer();
		port.SetName("Port");
		port.Value = new TransferOperation("localhost", "1080", "P_i2");
		pn.PlaceList.add(port);

		// Transitions
		// t_1: P_in -> lane1
		PetriTransition t1 = new PetriTransition(pn);
		t1.TransitionName = "t_1";
		t1.InputPlaceName.add("P_in");

		Condition t1Ct1 = new Condition(t1, "P_in", TransitionCondition.NotNull);
		GuardMapping grdT1 = new GuardMapping();
		grdT1.condition = t1Ct1;

		ArrayList<PlaceNameWithWeight> inputT1 = new ArrayList<>();
		inputT1.add(new PlaceNameWithWeight("P_in", 1.0F));
		ArrayList<String> outputT1 = new ArrayList<>();
		outputT1.add("lane1");
		grdT1.Activations.add(new Activation(t1, flrsMove, inputT1, TransitionOperation.FLRS, outputT1));
		t1.GuardMappingList.add(grdT1);
		t1.Delay = 0;
		pn.Transitions.add(t1);

		// t_move: lane1 -> lane2
		PetriTransition tMove = new PetriTransition(pn);
		tMove.TransitionName = "t_move";
		tMove.InputPlaceName.add("lane1");

		Condition tMoveCt1 = new Condition(tMove, "lane1", TransitionCondition.NotNull);
		GuardMapping grdTMove = new GuardMapping();
		grdTMove.condition = tMoveCt1;

		ArrayList<PlaceNameWithWeight> inputTMove = new ArrayList<>();
		inputTMove.add(new PlaceNameWithWeight("lane1", 1.0F));
		ArrayList<String> outputTMove = new ArrayList<>();
		outputTMove.add("lane2");
		grdTMove.Activations.add(new Activation(tMove, flrsMove, inputTMove, TransitionOperation.FLRS, outputTMove));
		tMove.GuardMappingList.add(grdTMove);
		tMove.Delay = 0;
		pn.Transitions.add(tMove);

		// t_out: lane2 + P_TL -> Port & P_TL Move P_TL
		PetriTransition tOut = new PetriTransition(pn);
		tOut.TransitionName = "t_out";
		tOut.InputPlaceName.add("lane2");
		tOut.InputPlaceName.add("P_TL");

		Condition tOutCt1 = new Condition(tOut, "P_TL", TransitionCondition.Equal, "green");
		Condition tOutCt2 = new Condition(tOut, "lane2", TransitionCondition.NotNull);
		tOutCt1.SetNextCondition(LogicConnector.AND, tOutCt2);

		GuardMapping grdTOut = new GuardMapping();
		grdTOut.condition = tOutCt1;
		grdTOut.Activations.add(new Activation(tOut, "lane2", TransitionOperation.SendOverNetwork, "Port"));
		grdTOut.Activations.add(new Activation(tOut, "P_TL", TransitionOperation.Move, "P_TL"));
		tOut.GuardMappingList.add(grdTOut);
		tOut.Delay = 1;
		pn.Transitions.add(tOut);

		// --------------------------------------------------------------------
		// ---------------------- B: Exit Lane Model --------------------------
		// --------------------------------------------------------------------
		DataFuzzy pE = new DataFuzzy();
		pE.SetName("P_e");
		pn.PlaceList.add(pE);

		DataFuzzy pExit = new DataFuzzy();
		pExit.SetName("P_Exit");
		pn.PlaceList.add(pExit);

		// Transition t_e1: P_e -> P_Exit
		PetriTransition tE1 = new PetriTransition(pn);
		tE1.TransitionName = "t_e1";
		tE1.InputPlaceName.add("P_e");

		Condition tE1Ct1 = new Condition(tE1, "P_e", TransitionCondition.NotNull);
		GuardMapping grdTE1 = new GuardMapping();
		grdTE1.condition = tE1Ct1;

		ArrayList<PlaceNameWithWeight> inputTE1 = new ArrayList<>();
		inputTE1.add(new PlaceNameWithWeight("P_e", 1.0F));
		ArrayList<String> outputTE1 = new ArrayList<>();
		outputTE1.add("P_Exit");
		grdTE1.Activations.add(new Activation(tE1, flrsMove, inputTE1, TransitionOperation.FLRS, outputTE1));
		tE1.GuardMappingList.add(grdTE1);
		tE1.Delay = 0;
		pn.Transitions.add(tE1);

		return pn;
	}

	public static void main(String[] args) {
		PetriNet pn = createPetriNet();

		System.out.println("Lane 2 started \n ------------------------------");
		pn.Delay = 2000;
		// pn.Start();

		PetriNetWindow frame = new PetriNetWindow(false);
		frame.petriNet = pn;
		frame.setVisible(true);
	}
}
