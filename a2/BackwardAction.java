package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class BackwardAction extends AbstractAction {

	Code c;
	
	BackwardAction(Code c) {
		this.c = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		c.moveBackward();
	}

}
