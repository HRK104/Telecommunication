package Resources;
/*
public class SomethingHappens {

}*/

import tcdIO.Terminal;

public class SomethingHappens {
	boolean buttonPressed= false;

	public class Output extends Thread {
		Terminal terminal;

		synchronized void printState() {
			try {
				wait(300);
			} catch (Exception e) {e.printStackTrace();}

			if (buttonPressed) {
				terminal.println("Someone pressed a button");
				buttonPressed= false;
			}
			else {
				terminal.println("Still nothing");
			}
		}

		public void run() {
			terminal= new Terminal("Output");

			for(;;)
				printState();
		}
	}

	public class Input extends Thread {
		Terminal terminal;

		synchronized void getInput() {
			terminal.readString();
			buttonPressed= true;
			notifyAll();
		}

		public void run() {
			//terminal= new Terminal("Output");
			terminal = new Terminal("Input");
			for(;;)
				getInput();
		}
	}

	void go() {
		buttonPressed= false;
		Input input= new Input();
		Output output= new Output();

		input.start();
		output.start();
	}

	public static void main(String[] var) {
		SomethingHappens program= new SomethingHappens();
		program.go();
	}
}
