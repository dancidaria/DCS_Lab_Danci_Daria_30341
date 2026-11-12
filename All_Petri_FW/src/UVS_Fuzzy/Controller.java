package UVS_Fuzzy;

import Components.Activation;
import Components.Condition;
import Components.GuardMapping;
import Components.PetriNet;
import Components.PetriNetWindow;
import Components.PetriTransition;
import DataObjects.DataFuzzy;
import DataObjects.DataString;
import DataObjects.DataTransfer;
import DataOnly.Fuzzy;
import DataOnly.TransferOperation;
import Enumerations.TransitionCondition;
import Enumerations.TransitionOperation;

public class Controller {

	public static PetriNet createPetriNet() {
		PetriNet pn = new PetriNet();
		pn.PetriNetName = "Controller";
		pn.SetName("Controller");
		pn.NetworkPort = 1085;

		//****************Traffic Light Fuzzy Conventions
		// Red signal: NL <1, 0, 0, 0, 0> = -1.0F
		// Green signal: ZR <0, 0, 1, 0, 0> =  0.0F
		// Yellow signal: PL <0, 0, 0, 0, 1> = +1.0F

		DataFuzzy ini = new DataFuzzy();
		ini.SetName("ini");
		ini.SetValue(new Fuzzy(-1.0F));
		ini.Printable = false;
		pn.ConstantPlaceList.add(ini);

		DataFuzzy red = new DataFuzzy();
		red.SetName("red");
		red.SetValue(new Fuzzy(-1.0F));
		red.Printable = false;
		pn.ConstantPlaceList.add(red);

		DataFuzzy green = new DataFuzzy();
		green.SetName("green");
		green.SetValue(new Fuzzy(0.0F));
		green.Printable = false;
		pn.ConstantPlaceList.add(green);

		DataFuzzy yellow = new DataFuzzy();
		yellow.SetName("yellow");
		yellow.SetValue(new Fuzzy(1.0F));
		yellow.Printable = false;
		pn.ConstantPlaceList.add(yellow);

		//*********************************************************Controller State Places
		DataString p1 = new DataString();
		p1.SetName("r1r2");
		p1.SetValue("signal");
		pn.PlaceList.add(p1);

		DataString p2 = new DataString();
		p2.SetName("g1r2");
		pn.PlaceList.add(p2);

		DataString p3 = new DataString();
		p3.SetName("y1r2");
		pn.PlaceList.add(p3);

		DataString p4 = new DataString();
		p4.SetName("r1g2");
		pn.PlaceList.add(p4);

		DataString p5 = new DataString();
		p5.SetName("r1y2");
		pn.PlaceList.add(p5);

		//*******************************************************************Output Ports to Lanes (P_TL)
		DataTransfer op1 = new DataTransfer();
		op1.SetName("OP1");
		op1.Value = new TransferOperation("localhost", "1081", "P_TL");
		pn.PlaceList.add(op1);

		DataTransfer op2 = new DataTransfer();
		op2.SetName("OP2");
		op2.Value = new TransferOperation("localhost", "1082", "P_TL");
		pn.PlaceList.add(op2);

		DataTransfer op3 = new DataTransfer();
		op3.SetName("OP3");
		op3.Value = new TransferOperation("localhost", "1083", "P_TL");
		pn.PlaceList.add(op3);

		DataTransfer op4 = new DataTransfer();
		op4.SetName("OP4");
		op4.Value = new TransferOperation("localhost", "1084", "P_TL");
		pn.PlaceList.add(op4);

		//*******************************************************Transitions
		//initialize all traffic lights to red (-1F)
		PetriTransition iniT = new PetriTransition(pn);
		iniT.TransitionName = "iniT";

		Condition iniTCt1 = new Condition(iniT, "ini", TransitionCondition.NotNull);
		GuardMapping grdIniT = new GuardMapping();
		grdIniT.condition = iniTCt1;
		grdIniT.Activations.add(new Activation(iniT, "ini", TransitionOperation.SendOverNetwork, "OP1"));
		grdIniT.Activations.add(new Activation(iniT, "ini", TransitionOperation.SendOverNetwork, "OP2"));
		grdIniT.Activations.add(new Activation(iniT, "ini", TransitionOperation.SendOverNetwork, "OP3"));
		grdIniT.Activations.add(new Activation(iniT, "ini", TransitionOperation.SendOverNetwork, "OP4"));
		grdIniT.Activations.add(new Activation(iniT, "", TransitionOperation.MakeNull, "ini"));
		iniT.GuardMappingList.add(grdIniT);
		iniT.Delay = 0;
		pn.Transitions.add(iniT);

		// T1: r1r2 -> g1r2 (Lanes 1 & 3 get Green)
		PetriTransition t1 = new PetriTransition(pn);
		t1.TransitionName = "T1";
		t1.InputPlaceName.add("r1r2");

		Condition t1Ct1 = new Condition(t1, "r1r2", TransitionCondition.NotNull);
		GuardMapping grdT1 = new GuardMapping();
		grdT1.condition = t1Ct1;
		grdT1.Activations.add(new Activation(t1, "r1r2", TransitionOperation.Move, "g1r2"));
		grdT1.Activations.add(new Activation(t1, "green", TransitionOperation.SendOverNetwork, "OP1"));
		grdT1.Activations.add(new Activation(t1, "green", TransitionOperation.SendOverNetwork, "OP3"));
		t1.GuardMappingList.add(grdT1);
		t1.Delay = 5;
		pn.Transitions.add(t1);

		// T2: g1r2 -> y1r2 (Lanes 1 & 3 get Yellow)
		PetriTransition t2 = new PetriTransition(pn);
		t2.TransitionName = "T2";
		t2.InputPlaceName.add("g1r2");

		Condition t2Ct1 = new Condition(t2, "g1r2", TransitionCondition.NotNull);
		GuardMapping grdT2 = new GuardMapping();
		grdT2.condition = t2Ct1;
		grdT2.Activations.add(new Activation(t2, "g1r2", TransitionOperation.Move, "y1r2"));
		grdT2.Activations.add(new Activation(t2, "yellow", TransitionOperation.SendOverNetwork, "OP1"));
		grdT2.Activations.add(new Activation(t2, "yellow", TransitionOperation.SendOverNetwork, "OP3"));
		t2.GuardMappingList.add(grdT2);
		t2.Delay = 5;
		pn.Transitions.add(t2);

		// T3: y1r2 -> r1g2 (Lanes 1 & 3 get Red, Lanes 2 & 4 get Green)
		PetriTransition t3 = new PetriTransition(pn);
		t3.TransitionName = "T3";
		t3.InputPlaceName.add("y1r2");

		Condition t3Ct1 = new Condition(t3, "y1r2", TransitionCondition.NotNull);
		GuardMapping grdT3 = new GuardMapping();
		grdT3.condition = t3Ct1;
		grdT3.Activations.add(new Activation(t3, "y1r2", TransitionOperation.Move, "r1g2"));
		grdT3.Activations.add(new Activation(t3, "red", TransitionOperation.SendOverNetwork, "OP1"));
		grdT3.Activations.add(new Activation(t3, "red", TransitionOperation.SendOverNetwork, "OP3"));
		grdT3.Activations.add(new Activation(t3, "green", TransitionOperation.SendOverNetwork, "OP2"));
		grdT3.Activations.add(new Activation(t3, "green", TransitionOperation.SendOverNetwork, "OP4"));
		t3.GuardMappingList.add(grdT3);
		t3.Delay = 5;
		pn.Transitions.add(t3);

		// T4: r1g2 -> r1y2 (Lanes 2 & 4 get Yellow)
		PetriTransition t4 = new PetriTransition(pn);
		t4.TransitionName = "T4";
		t4.InputPlaceName.add("r1g2");

		Condition t4Ct1 = new Condition(t4, "r1g2", TransitionCondition.NotNull);
		GuardMapping grdT4 = new GuardMapping();
		grdT4.condition = t4Ct1;
		grdT4.Activations.add(new Activation(t4, "r1g2", TransitionOperation.Move, "r1y2"));
		grdT4.Activations.add(new Activation(t4, "yellow", TransitionOperation.SendOverNetwork, "OP2"));
		grdT4.Activations.add(new Activation(t4, "yellow", TransitionOperation.SendOverNetwork, "OP4"));
		t4.GuardMappingList.add(grdT4);
		t4.Delay = 5;
		pn.Transitions.add(t4);

		// T5: r1y2 -> r1r2 (Lanes 2 & 4 get Red, loop repeats)
		PetriTransition t5 = new PetriTransition(pn);
		t5.TransitionName = "T5";
		t5.InputPlaceName.add("r1y2");

		Condition t5Ct1 = new Condition(t5, "r1y2", TransitionCondition.NotNull);
		GuardMapping grdT5 = new GuardMapping();
		grdT5.condition = t5Ct1;
		grdT5.Activations.add(new Activation(t5, "r1y2", TransitionOperation.Move, "r1r2"));
		grdT5.Activations.add(new Activation(t5, "red", TransitionOperation.SendOverNetwork, "OP2"));
		grdT5.Activations.add(new Activation(t5, "red", TransitionOperation.SendOverNetwork, "OP4"));
		t5.GuardMappingList.add(grdT5);
		t5.Delay = 5;
		pn.Transitions.add(t5);

		return pn;
	}

	public static void main(String[] args) {
		PetriNet pn = createPetriNet();

		System.out.println("Controller started \n ------------------------------");
		pn.Delay = 2000;
		// pn.Start();

		PetriNetWindow frame = new PetriNetWindow(false);
		frame.petriNet = pn;
		frame.setVisible(true);
	}
}
