/**
 * Note: based on code from https://github.com/ralfhergert/pc2-telemetry
 */
package io.ppatierno.formula1.pc2.helpers;

import java.util.Arrays;

/**
 * A vector of float.
 */
public class Vector {

	final float[] values;

	public Vector(float... values) {
		if (values == null) {
			throw new IllegalArgumentException("values can not be null");
		}
		this.values = values;
	}

	public float get(int index) {
		return values[index];
	}

	public double length() {
		double sum = 0;
		for (float value : values) {
			sum += value * value;
		}
		return Math.sqrt(sum);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Vector vector = (Vector) o;

		return Arrays.equals(values, vector.values);

	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(values);
	}
}
