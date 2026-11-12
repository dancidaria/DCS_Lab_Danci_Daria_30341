package DCS_FuzzyLab.RoomHeatingSystem;

import Components.*;
import DataObjects.DataFuzzy;
import DataObjects.DataTransfer;
import DataOnly.*;
import Enumerations.FZ;
import Enumerations.LogicConnector;
import Enumerations.TransitionCondition;
import Enumerations.TransitionOperation;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ORC {
    public static void main(String[] args) throws FileNotFoundException {
        PetriNet pn = new PetriNet();
        pn.PetriNetName = "ORC";
        pn.NetworkPort = 1084;

        pn.SetInputFile("C:\\Users\\user\\Desktop\\DCS_Lab_Danci_Daria_30341\\All_Petri_FW\\src\\DCS_FuzzyLab\\PetriInputData\\ORC.txt");

        FLRS readerTwoOutputs = new FLRS(
                new FV(FZ.NL, FZ.NL), new FV(FZ.NM, FZ.NM), new FV(FZ.ZR, FZ.ZR), new FV(FZ.PM, FZ.PM), new FV(FZ.PL, FZ.PL),
                new FV(FZ.NL, FZ.NL), new FV(FZ.NM, FZ.NM), new FV(FZ.ZR, FZ.ZR), new FV(FZ.PM, FZ.PM), new FV(FZ.PL, FZ.PL),
                new FV(FZ.NL, FZ.NL), new FV(FZ.NM, FZ.NM), new FV(FZ.ZR, FZ.ZR), new FV(FZ.PM, FZ.PM), new FV(FZ.PL, FZ.PL),
                new FV(FZ.NL, FZ.NL), new FV(FZ.NM, FZ.NM), new FV(FZ.ZR, FZ.ZR), new FV(FZ.PM, FZ.PM), new FV(FZ.PL, FZ.PL),
                new FV(FZ.NL, FZ.NL), new FV(FZ.NM, FZ.NM), new FV(FZ.ZR, FZ.ZR), new FV(FZ.PM, FZ.PM), new FV(FZ.PL, FZ.PL)
        );

        FLRS OneXOneDefaultTable = new FLRS(new FV(FZ.NL), new FV(FZ.NM), new FV(FZ.ZR), new FV(FZ.PM), new FV(FZ.PL));

        // Inverter FLRS for T2: Cold outside (NL) -> Hot water reference (PL)
        FLRS inverterTable = new FLRS(new FV(FZ.PL), new FV(FZ.PM), new FV(FZ.ZR), new FV(FZ.NM), new FV(FZ.NL));

        DataFuzzy p0 = new DataFuzzy(); p0.SetName("P0"); p0.SetValue(new Fuzzy(0.0F)); pn.PlaceList.add(p0);
        DataFuzzy p1 = new DataFuzzy(); p1.SetName("P1"); pn.PlaceList.add(p1); // outsideTemp
        DataFuzzy p2 = new DataFuzzy(); p2.SetName("P2"); pn.PlaceList.add(p2);
        DataFuzzy p3 = new DataFuzzy(); p3.SetName("P3"); pn.PlaceList.add(p3);
        DataFuzzy p4 = new DataFuzzy(); p4.SetName("P4"); pn.PlaceList.add(p4);

        DataTransfer p5 = new DataTransfer();
        p5.SetName("P5");
        p5.Value = new TransferOperation("localhost", "1083", "P1"); // sends waterTempRef to HTC
        pn.PlaceList.add(p5);

        // T0 ------------------------------------------------
        PetriTransition t0 = new PetriTransition(pn);
        t0.TransitionName = "T0";
        t0.InputPlaceName.add("P0");
        t0.InputPlaceName.add("P1");
        Condition T0Ct1 = new Condition(t0, "P0", TransitionCondition.NotNull);
        Condition T0Ct2 = new Condition(t0, "P1", TransitionCondition.NotNull);
        T0Ct1.SetNextCondition(LogicConnector.AND, T0Ct2);
        GuardMapping grdT0 = new GuardMapping();
        grdT0.condition = T0Ct1;
        ArrayList<PlaceNameWithWeight> input0 = new ArrayList<>();
        input0.add(new PlaceNameWithWeight("P0", 1F));
        input0.add(new PlaceNameWithWeight("P1", 1F));
        ArrayList<String> output0 = new ArrayList<>();
        output0.add("P2");
        output0.add("P3");
        grdT0.Activations.add(new Activation(t0, readerTwoOutputs, input0, TransitionOperation.FLRS, output0));
        t0.GuardMappingList.add(grdT0);
        t0.Delay = 0;
        pn.Transitions.add(t0);

        // T1 [1] --------------------------------------------
        PetriTransition t1 = new PetriTransition(pn);
        t1.TransitionName = "T1";
        t1.InputPlaceName.add("P2");
        Condition T1Ct1 = new Condition(t1, "P2", TransitionCondition.NotNull);
        GuardMapping grdT1 = new GuardMapping();
        grdT1.condition = T1Ct1;
        ArrayList<PlaceNameWithWeight> input1 = new ArrayList<>();
        input1.add(new PlaceNameWithWeight("P2", 1F));
        ArrayList<String> output1 = new ArrayList<>();
        output1.add("P0");
        grdT1.Activations.add(new Activation(t1, OneXOneDefaultTable, input1, TransitionOperation.FLRS, output1));
        t1.GuardMappingList.add(grdT1);
        t1.Delay = 1;
        pn.Transitions.add(t1);

        // T2 (FLRS) -----------------------------------------
        PetriTransition t2 = new PetriTransition(pn);
        t2.TransitionName = "T2";
        t2.InputPlaceName.add("P3");
        Condition T2Ct1 = new Condition(t2, "P3", TransitionCondition.NotNull);
        GuardMapping grdT2 = new GuardMapping();
        grdT2.condition = T2Ct1;
        ArrayList<PlaceNameWithWeight> input2 = new ArrayList<>();
        input2.add(new PlaceNameWithWeight("P3", 1F));
        ArrayList<String> output2 = new ArrayList<>();
        output2.add("P4");
        grdT2.Activations.add(new Activation(t2, inverterTable, input2, TransitionOperation.FLRS, output2));
        t2.GuardMappingList.add(grdT2);
        t2.Delay = 0;
        pn.Transitions.add(t2);

        // T3 (Send to HTC) ----------------------------------
        PetriTransition t3 = new PetriTransition(pn);
        t3.TransitionName = "T3";
        t3.InputPlaceName.add("P4");
        Condition T3Ct1 = new Condition(t3, "P4", TransitionCondition.NotNull);
        GuardMapping grdT3 = new GuardMapping();
        grdT3.condition = T3Ct1;
        grdT3.Activations.add(new Activation(t3, "P4", TransitionOperation.SendOverNetwork, "P5"));
        t3.GuardMappingList.add(grdT3);
        t3.Delay = 0;
        pn.Transitions.add(t3);

        System.out.println("ORC started \n ------------------------------");
        pn.Delay = 500;
        pn.ShowLogInWindow = true;

        PetriNetWindow frame = new PetriNetWindow(false);
        frame.petriNet = pn;
        frame.setVisible(true);
    }
}
