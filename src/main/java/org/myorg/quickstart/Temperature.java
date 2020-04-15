package org.myorg.quickstart;

/**
	 * Data type for words with count.
	 */
	public class Temperature {

		public String name;
		public long temperature;

		public Temperature() {}

		public Temperature(String name, long temperature) {
			this.name = name;
			this.temperature = temperature;
		}

		@Override
		public String toString() {
			return name + " : " + temperature;
		}
	}