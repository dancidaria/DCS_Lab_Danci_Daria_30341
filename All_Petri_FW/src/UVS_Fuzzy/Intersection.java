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

public class Intersection {

	public static PetriNet createPetriNet() {
		PetriNet pn = new PetriNet();
		pn.PetriNetName = "Intersection";
		pn.NetworkPort = 1080;
		FLRS flrsPlus = new FLRS(
				// Row 1 (NL):
				new FV(FZ.NL), new FV(FZ.NL), new FV(FZ.NL), new FV(FZ.NM), new FV(FZ.ZR),
				// Row 2 (NM):
				new FV(FZ.NL), new FV(FZ.NL), new FV(FZ.NM), new FV(FZ.ZR), new FV(FZ.PM),
				// Row 3 (ZR):
				new FV(FZ.NL), new FV(FZ.NM), new FV(FZ.ZR), new FV(FZ.PM), new FV(FZ.PL),
				// Row 4 (PM):
				new FV(FZ.NM), new FV(FZ.ZR), new FV(FZ.PM), new FV(FZ.PL), new FV(FZ.PL),
				// Row 5 (PL):
				new FV(FZ.ZR), new FV(FZ.PM), new FV(FZ.PL), new FV(FZ.PL), new FV(FZ.PL)
		);

		// constant places
		// P_o1 = P_I * 0.25
		// P_o2 = P_I * 0.50
		// P_o3 = P_I * 0.12
		// P_o4 = P_I * 0.12
		DataFuzzy c025 = new DataFuzzy();
		c025.SetName("c025");
		c025.SetValue(new Fuzzy(0.25F));
		c025.Printable = false;
		pn.ConstantPlaceList.add(c025);

		DataFuzzy c050 = new DataFuzzy();
		c050.SetName("c050");
		c050.SetValue(new Fuzzy(0.50F));
		c050.Printable = false;
		pn.ConstantPlaceList.add(c050);

		DataFuzzy c012 = new DataFuzzy();
		c012.SetName("c012");
		c012.SetValue(new Fuzzy(0.12F));
		c012.Printable = false;
		pn.ConstantPlaceList.add(c012);

		// ******************************************places
		//input places
		DataFuzzy pI1 = new DataFuzzy();
		pI1.SetName("P_i1");
		pn.PlaceList.add(pI1);

		DataFuzzy pI2 = new DataFuzzy();
		pI2.SetName("P_i2");
		pn.PlaceList.add(pI2);

		DataFuzzy pI3 = new DataFuzzy();
		pI3.SetName("P_i3");
		pn.PlaceList.add(pI3);

		DataFuzzy pI4 = new DataFuzzy();
		pI4.SetName("P_i4");
		pn.PlaceList.add(pI4);

		//intermediate summation places
		DataFuzzy pT1 = new DataFuzzy();
		pT1.SetName("p_t1");
		pn.PlaceList.add(pT1);

		DataFuzzy pT2 = new DataFuzzy();
		pT2.SetName("p_t2");
		pn.PlaceList.add(pT2);

		//central intersection place
		DataFuzzy pI = new DataFuzzy();
		pI.SetName("P_I");
		pn.PlaceList.add(pI);

		//split output places
		DataFuzzy pO1 = new DataFuzzy();
		pO1.SetName("P_o1");
		pn.PlaceList.add(pO1);

		DataFuzzy pO2 = new DataFuzzy();
		pO2.SetName("P_o2");
		pn.PlaceList.add(pO2);

		DataFuzzy pO3 = new DataFuzzy();
		pO3.SetName("P_o3");
		pn.PlaceList.add(pO3);

		DataFuzzy pO4 = new DataFuzzy();
		pO4.SetName("P_o4");
		pn.PlaceList.add(pO4);

		//output DataTransfer places to exit lanes
		DataTransfer pOut1 = new DataTransfer();
		pOut1.SetName("P_out1");
		pOut1.Value = new TransferOperation("localhost", "1081", "P_e");
		pn.PlaceList.add(pOut1);

		DataTransfer pOut2 = new DataTransfer();
		pOut2.SetName("P_out2");
		pOut2.Value = new TransferOperation("localhost", "1082", "P_e");
		pn.PlaceList.add(pOut2);

		DataTransfer pOut3 = new DataTransfer();
		pOut3.SetName("P_out3");
		pOut3.Value = new TransferOperation("localhost", "1083", "P_e");
		pn.PlaceList.add(pOut3);

		DataTransfer pOut4 = new DataTransfer();
		pOut4.SetName("P_out4");
		pOut4.Value = new TransferOperation("localhost", "1084", "P_e");
		pn.PlaceList.add(pOut4);

		//transitions
		// t1: P_i1 + P_i2 -> p_t1
		PetriTransition t1 = new PetriTransition(pn);
		t1.TransitionName = "t1";
		t1.InputPlaceName.add("P_i1");
		t1.InputPlaceName.add("P_i2");

		Condition t1Ct1 = new Condition(t1, "P_i1", TransitionCondition.NotNull);
		Condition t1Ct2 = new Condition(t1, "P_i2", TransitionCondition.NotNull);
		t1Ct1.SetNextCondition(LogicConnector.AND, t1Ct2);

		GuardMapping grdT1 = new GuardMapping();
		grdT1.condition = t1Ct1;

		ArrayList<PlaceNameWithWeight> inT1 = new ArrayList<>();
		inT1.add(new PlaceNameWithWeight("P_i1", 1.0F));
		inT1.add(new PlaceNameWithWeight("P_i2", 1.0F));
		ArrayList<String> outT1 = new ArrayList<>();
		outT1.add("p_t1");
		grdT1.Activations.add(new Activation(t1, flrsPlus, inT1, TransitionOperation.FLRS, outT1));
		t1.GuardMappingList.add(grdT1);
		t1.Delay = 0;
		pn.Transitions.add(t1);

		// t2: P_i3 + P_i4 -> p_t2
		PetriTransition t2 = new PetriTransition(pn);
		t2.TransitionName = "t2";
		t2.InputPlaceName.add("P_i3");
		t2.InputPlaceName.add("P_i4");

		Condition t2Ct1 = new Condition(t2, "P_i3", TransitionCondition.NotNull);
		Condition t2Ct2 = new Condition(t2, "P_i4", TransitionCondition.NotNull);
		t2Ct1.SetNextCondition(LogicConnector.AND, t2Ct2);

		GuardMapping grdT2 = new GuardMapping();
		grdT2.condition = t2Ct1;

		ArrayList<PlaceNameWithWeight> inT2 = new ArrayList<>();
		inT2.add(new PlaceNameWithWeight("P_i3", 1.0F));
		inT2.add(new PlaceNameWithWeight("P_i4", 1.0F));
		ArrayList<String> outT2 = new ArrayList<>();
		outT2.add("p_t2");
		grdT2.Activations.add(new Activation(t2, flrsPlus, inT2, TransitionOperation.FLRS, outT2));
		t2.GuardMappingList.add(grdT2);
		t2.Delay = 0;
		pn.Transitions.add(t2);

		// t3: p_t1 + p_t2 -> P_I
		PetriTransition t3 = new PetriTransition(pn);
		t3.TransitionName = "t3";
		t3.InputPlaceName.add("p_t1");
		t3.InputPlaceName.add("p_t2");

		Condition t3Ct1 = new Condition(t3, "p_t1", TransitionCondition.NotNull);
		Condition t3Ct2 = new Condition(t3, "p_t2", TransitionCondition.NotNull);
		t3Ct1.SetNextCondition(LogicConnector.AND, t3Ct2);

		GuardMapping grdT3 = new GuardMapping();
		grdT3.condition = t3Ct1;

		ArrayList<PlaceNameWithWeight> inT3 = new ArrayList<>();
		inT3.add(new PlaceNameWithWeight("p_t1", 1.0F));
		inT3.add(new PlaceNameWithWeight("p_t2", 1.0F));
		ArrayList<String> outT3 = new ArrayList<>();
		outT3.add("P_I");
		grdT3.Activations.add(new Activation(t3, flrsPlus, inT3, TransitionOperation.FLRS, outT3));
		t3.GuardMappingList.add(grdT3);
		t3.Delay = 0;
		pn.Transitions.add(t3);

		// t_s: Split P_I using fuzzy mathematical operations
		PetriTransition tS = new PetriTransition(pn);
		tS.TransitionName = "t_s";
		tS.InputPlaceName.add("P_I");

		Condition tSCt1 = new Condition(tS, "P_I", TransitionCondition.NotNull);
		GuardMapping grdTS = new GuardMapping();
		grdTS.condition = tSCt1;

		// P_o1 = P_I * 0.25
		ArrayList<PlaceNameWithWeight> inS1 = new ArrayList<>();
		inS1.add(new PlaceNameWithWeight("P_I", 1.0F));
		inS1.add(new PlaceNameWithWeight("c025", 1.0F));
		grdTS.Activations.add(new Activation(inS1, TransitionOperation.Prod_Fuzzy, "P_o1", tS));

		// P_o2 = P_I * 0.50
		ArrayList<PlaceNameWithWeight> inS2 = new ArrayList<>();
		inS2.add(new PlaceNameWithWeight("P_I", 1.0F));
		inS2.add(new PlaceNameWithWeight("c050", 1.0F));
		grdTS.Activations.add(new Activation(inS2, TransitionOperation.Prod_Fuzzy, "P_o2", tS));

		// P_o3 = P_I * 0.12
		ArrayList<PlaceNameWithWeight> inS3 = new ArrayList<>();
		inS3.add(new PlaceNameWithWeight("P_I", 1.0F));
		inS3.add(new PlaceNameWithWeight("c012", 1.0F));
		grdTS.Activations.add(new Activation(inS3, TransitionOperation.Prod_Fuzzy, "P_o3", tS));

		// P_o4 = P_I * 0.12
		ArrayList<PlaceNameWithWeight> inS4 = new ArrayList<>();
		inS4.add(new PlaceNameWithWeight("P_I", 1.0F));
		inS4.add(new PlaceNameWithWeight("c012", 1.0F));
		grdTS.Activations.add(new Activation(inS4, TransitionOperation.Prod_Fuzzy, "P_o4", tS));

		tS.GuardMappingList.add(grdTS);
		tS.Delay = 0;
		pn.Transitions.add(tS);

		// t_out1: P_o1 -> SendOverNetwork to P_out1
		PetriTransition tOut1 = new PetriTransition(pn);
		tOut1.TransitionName = "t_out1";
		tOut1.InputPlaceName.add("P_o1");
		Condition tOut1Ct = new Condition(tOut1, "P_o1", TransitionCondition.NotNull);
		GuardMapping grdTOut1 = new GuardMapping();
		grdTOut1.condition = tOut1Ct;
		grdTOut1.Activations.add(new Activation(tOut1, "P_o1", TransitionOperation.SendOverNetwork, "P_out1"));
		tOut1.GuardMappingList.add(grdTOut1);
		tOut1.Delay = 0;
		pn.Transitions.add(tOut1);

		// t_out2: P_o2 -> SendOverNetwork to P_out2
		PetriTransition tOut2 = new PetriTransition(pn);
		tOut2.TransitionName = "t_out2";
		tOut2.InputPlaceName.add("P_o2");
		Condition tOut2Ct = new Condition(tOut2, "P_o2", TransitionCondition.NotNull);
		GuardMapping grdTOut2 = new GuardMapping();
		grdTOut2.condition = tOut2Ct;
		grdTOut2.Activations.add(new Activation(tOut2, "P_o2", TransitionOperation.SendOverNetwork, "P_out2"));
		tOut2.GuardMappingList.add(grdTOut2);
		tOut2.Delay = 0;
		pn.Transitions.add(tOut2);

		// t_out3: P_o3 -> SendOverNetwork to P_out3
		PetriTransition tOut3 = new PetriTransition(pn);
		tOut3.TransitionName = "t_out3";
		tOut3.InputPlaceName.add("P_o3");
		Condition tOut3Ct = new Condition(tOut3, "P_o3", TransitionCondition.NotNull);
		GuardMapping grdTOut3 = new GuardMapping();
		grdTOut3.condition = tOut3Ct;
		grdTOut3.Activations.add(new Activation(tOut3, "P_o3", TransitionOperation.SendOverNetwork, "P_out3"));
		tOut3.GuardMappingList.add(grdTOut3);
		tOut3.Delay = 0;
		pn.Transitions.add(tOut3);

		// t_out4: P_o4 -> SendOverNetwork to P_out4
		PetriTransition tOut4 = new PetriTransition(pn);
		tOut4.TransitionName = "t_out4";
		tOut4.InputPlaceName.add("P_o4");
		Condition tOut4Ct = new Condition(tOut4, "P_o4", TransitionCondition.NotNull);
		GuardMapping grdTOut4 = new GuardMapping();
		grdTOut4.condition = tOut4Ct;
		grdTOut4.Activations.add(new Activation(tOut4, "P_o4", TransitionOperation.SendOverNetwork, "P_out4"));
		tOut4.GuardMappingList.add(grdTOut4);
		tOut4.Delay = 0;
		pn.Transitions.add(tOut4);

		return pn;
	}

	public static void main(String[] args) {
		PetriNet pn = createPetriNet();

		System.out.println("Intersection started \n ------------------------------");
		pn.Delay = 2000;
		// pn.Start();

		PetriNetWindow frame = new PetriNetWindow(false);
		frame.petriNet = pn;
		frame.setVisible(true);
	}
}
