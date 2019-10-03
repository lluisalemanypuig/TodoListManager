/*********************************************************************
 *
 *  TodoListManager - Open-source manager of todo lists
 *
 *  Copyright (C) 2019
 *
 *  This file is part of TodoListManager.
 *
 *  TodoListManager is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TodoListManager is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Treeler.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Contact: Lluís Alemany Puig (lluis.alemany.puig@gmail.com)
 *
 ********************************************************************/

package gui;

import java.awt.Component;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import todomanager.task.Task;
import todomanager.task.TaskStateEnum;

/**
 * @brief Class used to display a particular icon for each tree node.
 * @author Lluís Alemany Puig
 */
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
	private ImageIcon icon_cancelled;
	private ImageIcon icon_deleted;
	private ImageIcon icon_done;
	private ImageIcon icon_on_revision;
	private ImageIcon icon_opened;
	private ImageIcon icon_pending_revision;
	private ImageIcon icon_put_on_hold;
	private ImageIcon icon_working;
	
	public CustomTreeCellRenderer(int s) {
		String cur_dir = System.getProperty("user.dir") + "/TodoListManagerData/";
		
		icon_cancelled = new ImageIcon(cur_dir + "imgs/cancelled.png");
		icon_cancelled = new ImageIcon(icon_cancelled.getImage().getScaledInstance(s, s, java.awt.Image.SCALE_SMOOTH));
		
		icon_deleted = new ImageIcon(cur_dir + "imgs/deleted.png");
		icon_deleted = new ImageIcon(icon_deleted.getImage().getScaledInstance(s, s, java.awt.Image.SCALE_SMOOTH));
		
		icon_done = new ImageIcon(cur_dir + "imgs/done.png");
		icon_done = new ImageIcon(icon_done.getImage().getScaledInstance(s, s, java.awt.Image.SCALE_SMOOTH));
		
		icon_on_revision = new ImageIcon(cur_dir + "imgs/on_revision.png");
		icon_on_revision = new ImageIcon(icon_on_revision.getImage().getScaledInstance(s, s, java.awt.Image.SCALE_SMOOTH));
		
		icon_opened = new ImageIcon(cur_dir + "imgs/opened.png");
		icon_opened = new ImageIcon(icon_opened.getImage().getScaledInstance(s, s, java.awt.Image.SCALE_SMOOTH));
		
		icon_pending_revision = new ImageIcon(cur_dir + "imgs/pending_revision.png");
		icon_pending_revision = new ImageIcon(icon_pending_revision.getImage().getScaledInstance(s, s, java.awt.Image.SCALE_SMOOTH));
		
		icon_put_on_hold = new ImageIcon(cur_dir + "imgs/put_on_hold.png");
		icon_put_on_hold = new ImageIcon(icon_put_on_hold.getImage().getScaledInstance(s, s, java.awt.Image.SCALE_SMOOTH));
		
		icon_working = new ImageIcon(cur_dir + "imgs/working.png");
		icon_working = new ImageIcon(icon_working.getImage().getScaledInstance(s, s, java.awt.Image.SCALE_SMOOTH));
	}

	@Override
	public Component getTreeCellRendererComponent(
		JTree tree, Object value,
		boolean sel, boolean expanded,
		boolean leaf, int row, boolean hasFocus
	)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		
		// the first two levels will never contain tasks
		if (node.getLevel() <= 1) { return this; }
		
		Task t = (Task) node.getUserObject();
		TaskStateEnum s = t.currentState().getState();
		
		setToolTipText(s.toString());
		switch (s) {
			case Cancelled:			setIcon(icon_cancelled); break;
			case Deleted:			setIcon(icon_deleted); break;
			case Done:				setIcon(icon_done); break;
			case OnRevision:		setIcon(icon_on_revision); break;
			case Opened:			setIcon(icon_opened); break;
			case PendingRevision:	setIcon(icon_pending_revision); break;
			case PutOnHold:			setIcon(icon_put_on_hold); break;
			case Working:			setIcon(icon_working); break;
			default:
				// do nothing
		}
		return this;
	}
}
