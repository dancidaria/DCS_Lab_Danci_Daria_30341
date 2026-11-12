package DataOnly;

import java.io.Serializable;

public class Fuzzy implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Fuzzy clone() throws CloneNotSupportedException {
		return (Fuzzy) super.clone();
	}

	public FuzzyVector Vector;
	public Float Value;

	public Fuzzy(FuzzyVector vector, float value) {
		this.Vector = vector;
		this.Value = value;
		GenerateFuzzyToken(this.Value);
	}

	public Fuzzy(float value) {
		this.Vector = new FuzzyVector(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		this.Value = value;
		GenerateFuzzyToken(this.Value);
	}

	public String toString() {
		return "(" + Value.toString() + ": " + Vector.toString() + ")";
	}

	public void GenerateFuzzyToken(Float v) {
		Vector.GenerateFuzzyToken(v);
		Value = v;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof Fuzzy) {
			Fuzzy other = (Fuzzy) obj;
			if (this.Value == null && other.Value == null)
				return true;
			if (this.Value != null && other.Value != null) {
				return Math.abs(this.Value - other.Value) < 0.0001f;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Value != null ? Value.hashCode() : 0;
	}


}