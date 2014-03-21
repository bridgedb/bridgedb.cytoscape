/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package org.bridgedb.cytoscape.internal.ui;

import org.bridgedb.cytoscape.internal.IDMapperClient;
import org.bridgedb.cytoscape.internal.IDMapperClientImpl;

import org.bridgedb.IDMapperException;

import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.io.CyFileFilter;

import java.io.File;
import java.util.Collections;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.cytoscape.util.swing.FileChooserFilter;

/**
 * 
 * @author gjj
 */
public class RDBIDMappingClientConfigDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9140043093001072657L;

	private enum DBType {
		PGDB("BridgeDb Derby Embeded (.bridge)");

		DBType(String name) {
			this.name = name;
		}

		String name;

		public String toString() {
			return name;
		}
	}

	/** Creates new form RDBIDMappingClientConfig */
	public RDBIDMappingClientConfigDialog(javax.swing.JDialog parent,
			FileUtil fileUtil, boolean modal) {
		super(parent, modal);
                this.fileUtil = fileUtil;
		initComponents();
	}

	// public RDBIDMappingClientConfigDialog(javax.swing.JDialog parent, boolean
	// modal,
	// IDMapperClient client) {
	// super(parent, modal);
	// initComponents();
	// this.client = client;
	// if (client!=null) {
	// IDMapperRdb idMapper = (IDMapperRdb)client.getIDMapper();
	// if (idMapper instanceof SimpleGdb) {
	// //TODO: initialize
	// }
	// }
	// }

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	
	// <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JPanel namePanel = new javax.swing.JPanel();
        nameComboBox = new javax.swing.JComboBox();
        pgdbPanel = new javax.swing.JPanel();
        pgdbTextField = new javax.swing.JTextField();
        javax.swing.JButton pgdbButton = new javax.swing.JButton();
        javax.swing.JPanel okPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("RDB-based ID Mapping Resources Configuration");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        namePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Relational Database Type"));
        namePanel.setLayout(new java.awt.GridBagLayout());

        nameComboBox.setModel(new DefaultComboBoxModel(DBType.values()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        namePanel.add(nameComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(namePanel, gridBagConstraints);

        pgdbPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("BridgeDb File (.bridge)"));
        pgdbPanel.setLayout(new java.awt.GridBagLayout());

        pgdbTextField.setPreferredSize(new java.awt.Dimension(250, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pgdbPanel.add(pgdbTextField, gridBagConstraints);

        pgdbButton.setText("Select");
        pgdbButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pgdbButtonActionPerformed(evt);
            }
        });
        pgdbPanel.add(pgdbButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(pgdbPanel, gridBagConstraints);

        okPanel.setLayout(new javax.swing.BoxLayout(okPanel, javax.swing.BoxLayout.LINE_AXIS));

        okButton.setText("   OK   ");
        okButton.setToolTipText("");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        okPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(okPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void pgdbButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pgdbButtonActionPerformed
            FileChooserFilter fileChooserFilter = new FileChooserFilter(
                    "BridgeDb Derby file", new String[]{"bridge"});
            File source = fileUtil.getFile(this, "Select a BridgeDb file", FileUtil.LOAD,
                    Collections.singleton(fileChooserFilter));
            if (source==null) {
                return;
            }
	
            pgdbTextField.setText(source.getPath());
	}// GEN-LAST:event_pgdbButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
		setVisible(false);
	}// GEN-LAST:event_cancelButtonActionPerformed

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okButtonActionPerformed
		if (verifyInput()) {
			cancelled = false;
			setVisible(false);
		}
	}// GEN-LAST:event_okButtonActionPerformed

	public IDMapperClient getIDMappingClient() throws ClassNotFoundException,
			IDMapperException {
		String[] strs = getSettings();
		String connStr = strs[0];
		String className = strs[1];
		String displayName = strs[2];

		return new IDMapperClientImpl.Builder(connStr, className).displayName(
				displayName).build();
	}

	private String[] getSettings() {
		String connString, className;
		DBType type = (DBType) nameComboBox.getSelectedItem();
		if (type == DBType.PGDB) {
			String url = pgdbTextField.getText();
			connString = "idmapper-pgdb:" + url;
			className = "org.bridgedb.rdb.IDMapperRdb";
			return new String[] { connString, className, url };
		} else {
			throw new java.lang.IllegalStateException();
		}
	}

	private boolean verifyInput() {
		DBType type = (DBType) nameComboBox.getSelectedItem();
		if (type == DBType.PGDB) {
			String strURL = pgdbTextField.getText();
			if (strURL == null || strURL.length() == 0) {
				JOptionPane.showMessageDialog(this,
						"Error: Please specify the URL of the input file");
				return false;
			}

			File file = new File(strURL);
			if (!file.exists()) {
				JOptionPane.showMessageDialog(this,
						"Error: the input file does not exist");
				return false;
			}
		}

		return true;
	}

	public boolean isCancelled() {
		return cancelled;
	}

        private final FileUtil fileUtil;
	private boolean cancelled = true;
	// private IDMapperClient client;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox nameComboBox;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel pgdbPanel;
    private javax.swing.JTextField pgdbTextField;
    // End of variables declaration//GEN-END:variables

}
