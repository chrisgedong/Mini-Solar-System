package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class DownAction extends AbstractAction {

	Code c;
	
	DownAction(Code c) {
		this.c = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		c.moveDown();
	}

}
