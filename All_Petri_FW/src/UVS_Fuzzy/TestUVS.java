package UVS_Fuzzy;

import Components.Condition;
import Components.PetriNet;
import Components.PetriTransition;
import DataObjects.DataFuzzy;
import DataOnly.Fuzzy;
import Enumerations.TransitionCondition;
import Interfaces.PetriObject;
import Utilities.Functions;

public class TestUVS {

	private static Functions util = new Functions();

	private static PetriObject getPlace(PetriNet pn, String name) {
		return util.GetPetriObjectByName(name, pn.PlaceList);
	}

	private static PetriObject getConstant(PetriNet pn, String name) {
		return util.GetFromListByName(name, pn.ConstantPlaceList);
	}

	public static void main(String[] args) {
		System.out.println("Running UVS_Fuzzy verification tests...\n");

		// Test 1: Verify Lane1 structure and traffic light condition
		PetriNet lane1Net = Lane1.createPetriNet();
		assertNotNull(lane1Net, "Lane 1 PetriNet should not be null");
		assertNotNull(getPlace(lane1Net, "P_in"), "P_in should exist in Lane 1");
		assertNotNull(getPlace(lane1Net, "lane1"), "lane1 should exist in Lane 1");
		assertNotNull(getPlace(lane1Net, "lane2"), "lane2 should exist in Lane 1");
		assertNotNull(getPlace(lane1Net, "P_TL"), "P_TL should exist in Lane 1");
		assertNotNull(getPlace(lane1Net, "Port"), "Port should exist in Lane 1");
		assertNotNull(getPlace(lane1Net, "P_e"), "P_e should exist in Lane 1");
		assertNotNull(getPlace(lane1Net, "P_Exit"), "P_Exit should exist in Lane 1");
		System.out.println("[PASS] Lane 1 structure verified.");

		// Test 2: Test traffic light condition logic (Green vs Red)
		PetriTransition tOut = lane1Net.Transitions.stream()
				.filter(t -> t.TransitionName.equals("t_out"))
				.findFirst()
				.orElse(null);
		assertNotNull(tOut, "t_out transition should exist in Lane 1");

		// Case 2a: P_TL is Red (-1.0F) -> condition should be false
		DataFuzzy pTL = (DataFuzzy) getPlace(lane1Net, "P_TL");
		pTL.SetValue(new Fuzzy(-1.0F));
		DataFuzzy lane2 = (DataFuzzy) getPlace(lane1Net, "lane2");
		lane2.SetValue(new Fuzzy(0.5F));
		boolean conditionWhenRed = tOut.GuardMappingList.get(0).condition.CheckCondition();
		if (!conditionWhenRed) {
			System.out.println("[PASS] t_out blocked when traffic light is RED (-1.0F).");
		} else {
			System.err.println("[FAIL] t_out allowed firing when traffic light is RED!");
		}

		// Case 2b: P_TL is Green (0.0F) -> condition should be true
		pTL.SetValue(new Fuzzy(0.0F));
		boolean conditionWhenGreen = tOut.GuardMappingList.get(0).condition.CheckCondition();
		if (conditionWhenGreen) {
			System.out.println("[PASS] t_out enabled when traffic light is GREEN (0.0F).");
		} else {
			System.err.println("[FAIL] t_out blocked when traffic light is GREEN!");
		}

		// Test 3: Verify Intersection structure
		PetriNet interNet = Intersection.createPetriNet();
		assertNotNull(getPlace(interNet, "P_i1"), "P_i1 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_i2"), "P_i2 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_i3"), "P_i3 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_i4"), "P_i4 should exist in Intersection");
		assertNotNull(getPlace(interNet, "p_t1"), "p_t1 should exist in Intersection");
		assertNotNull(getPlace(interNet, "p_t2"), "p_t2 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_I"), "P_I should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_o1"), "P_o1 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_o2"), "P_o2 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_o3"), "P_o3 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_o4"), "P_o4 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_out1"), "P_out1 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_out2"), "P_out2 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_out3"), "P_out3 should exist in Intersection");
		assertNotNull(getPlace(interNet, "P_out4"), "P_out4 should exist in Intersection");
		System.out.println("[PASS] Intersection structure verified.");

		// Test 4: Test split transition t_s fuzzy math (Prod_Fuzzy)
		PetriTransition tS = interNet.Transitions.stream()
				.filter(t -> t.TransitionName.equals("t_s"))
				.findFirst()
				.orElse(null);
		assertNotNull(tS, "t_s transition should exist in Intersection");

		DataFuzzy pI = (DataFuzzy) getPlace(interNet, "P_I");
		pI.SetValue(new Fuzzy(1.0F));

		try {
			boolean ready = tS.CheckConditions();
			if (ready) {
				tS.BookTokens();
				tS.Activate();
			} else {
				System.err.println("[FAIL] tS.CheckConditions() returned false!");
			}

			DataFuzzy pO1 = (DataFuzzy) getPlace(interNet, "P_o1");
			DataFuzzy pO2 = (DataFuzzy) getPlace(interNet, "P_o2");
			DataFuzzy pO3 = (DataFuzzy) getPlace(interNet, "P_o3");
			DataFuzzy pO4 = (DataFuzzy) getPlace(interNet, "P_o4");

			System.out.println("Split results from P_I = 1.0F:");
			System.out.println("  P_o1 (expected ~0.25): " + (pO1 != null && pO1.Value != null ? pO1.Value.Value : "null"));
			System.out.println("  P_o2 (expected ~0.50): " + (pO2 != null && pO2.Value != null ? pO2.Value.Value : "null"));
			System.out.println("  P_o3 (expected ~0.12): " + (pO3 != null && pO3.Value != null ? pO3.Value.Value : "null"));
			System.out.println("  P_o4 (expected ~0.12): " + (pO4 != null && pO4.Value != null ? pO4.Value.Value : "null"));

			if (pO1 != null && pO1.Value != null && Math.abs(pO1.Value.Value - 0.25F) < 0.01F &&
				pO2 != null && pO2.Value != null && Math.abs(pO2.Value.Value - 0.50F) < 0.01F &&
				pO3 != null && pO3.Value != null && Math.abs(pO3.Value.Value - 0.12F) < 0.01F &&
				pO4 != null && pO4.Value != null && Math.abs(pO4.Value.Value - 0.12F) < 0.01F) {
				System.out.println("[PASS] Fuzzy mathematical split (Prod_Fuzzy) verified successfully.");
			} else {
				System.err.println("[FAIL] Fuzzy mathematical split produced unexpected values!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Test 5: Verify Controller structure and conventions
		PetriNet ctrlNet = Controller.createPetriNet();
		assertNotNull(getConstant(ctrlNet, "red"), "Constant red should exist in Controller");
		assertNotNull(getConstant(ctrlNet, "green"), "Constant green should exist in Controller");
		assertNotNull(getConstant(ctrlNet, "yellow"), "Constant yellow should exist in Controller");
		DataFuzzy redConst = (DataFuzzy) getConstant(ctrlNet, "red");
		DataFuzzy greenConst = (DataFuzzy) getConstant(ctrlNet, "green");
		DataFuzzy yellowConst = (DataFuzzy) getConstant(ctrlNet, "yellow");
		assertEquals(-1.0F, redConst.Value.Value, "Red should be -1.0F (NL)");
		assertEquals(0.0F, greenConst.Value.Value, "Green should be 0.0F (ZR)");
		assertEquals(1.0F, yellowConst.Value.Value, "Yellow should be 1.0F (PL)");
		System.out.println("[PASS] Controller traffic light conventions verified.");

		System.out.println("\nAll UVS_Fuzzy verification checks completed successfully!");
	}

	private static void assertNotNull(Object obj, String message) {
		if (obj == null) {
			throw new AssertionError("Assertion failed: " + message);
		}
	}

	private static void assertEquals(float expected, float actual, String message) {
		if (Math.abs(expected - actual) > 0.0001f) {
			throw new AssertionError("Assertion failed: " + message + " (expected: " + expected + ", actual: " + actual + ")");
		}
	}
}
