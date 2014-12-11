import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class BallotUI implements ActionListener {
//OLD, NOT USED ANYMORE
    private Setup setup;
    private JButton enter;
    private JButton quit;
    private JButton select;
    private JTextField codeField;
    private JComboBox<String> pcands;
    private JComboBox<String> vcands;
    private JComboBox<String> ccands;
    private JComboBox<String> cands;
    private JLabel instruc;
    private JFrame frame;
    private HelperMethods helper;
    private String code;
    private boolean valid;
    private String presresult;
    private String vpresult;
    private String curesult;
    private String result;

    public void run() {
        valid = false;
        //set up frame
        frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(new Dimension(1000, 1000));
        frame.setTitle("UCLS Voting");
        frame.setVisible(true);
        Color maroon = new Color(128, 0, 0);
        frame.getContentPane().setBackground(maroon);
        //set up buttons
        quit = new JButton("End All Voting");
        quit.addActionListener(this);
        codeField = new JTextField(6);
        instruc = new JLabel("Enter 6-digit code (case sensitive): ");
        enter = new JButton("Enter");
        enter.addActionListener(this);

        //set up layout
        //JPanel North = new JPanel(new GridLayout(3,1));
        frame.add(instruc);
        frame.add(codeField);
        frame.add(enter);
        frame.add(quit);
    }

    public void Managment() {
        setup = new Setup();
        String link = JOptionPane.showInputDialog("Enter Google Acount Username: ");
        while (link == null || link.equals("")) {
            link = JOptionPane.showInputDialog("Enter Google Acount Username: ");
        }
        String password = JOptionPane.showInputDialog("Enter Google Acount Password: ");
        while (password == null || link.equals("")) {
            password = JOptionPane.showInputDialog("Please Enter Acount Password: ");
        }
        setup.getDriveVals(link,password);
        helper = new HelperMethods(setup.getLink(), setup.getPassword());
        setup.inputCands();

    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == enter) {
            if (helper.checkIfValid(codeField.getText())) {
                valid = true;
                JOptionPane.showMessageDialog(null, "Success!");
                code = codeField.getText();
                enter.setVisible(false);
                codeField.setVisible(false);
                instruc.setVisible(false);
                //adds candidate selection dialog
                setupCandidateSelection();

            } else {
                instruc.setText("You must have already voted or entered your ID in wrong");
            }
        } else if (src == quit) {
            String input = JOptionPane.showInputDialog(null, "Enter Google Drive Password: ");
            if (input.equals(setup.getPassword())) {
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(frame, "Wrong Password!");
            }
        } else if (src == select && valid) {
            presresult = (String) pcands.getSelectedItem();
            vpresult = (String) vcands.getSelectedItem();
            curesult = (String) ccands.getSelectedItem();
            result = (String) cands.getSelectedItem();
            voter();
        } else if (src == select && !valid) {
            JOptionPane.showMessageDialog(frame, "You must enter your code");

        }
    }

    private void setupCandidateSelection(){
        String[] poptions = setup.getPresCandidates().toArray(new String[setup.getPresCandidates().size()]);
        pcands = new JComboBox<String>(poptions);
        pcands.setEditable(true);
        frame.add(pcands);

        String[] voptions = setup.getVpCandidates().toArray(new String[setup.getVpCandidates().size()]);
        vcands = new JComboBox<String>(voptions);
        vcands.setEditable(true);
        frame.add(vcands);

        String[] coptions1 = setup.getCuCandidates().toArray(new String[setup.getCuCandidates().size()]);
        ccands = new JComboBox<String>(coptions1);
        ccands.setEditable(true);
        frame.add(ccands);

        String[] coptions2 = setup.getCuCandidates().toArray(new String[setup.getCuCandidates().size()]);
        cands = new JComboBox<String>(coptions2);
        cands.setEditable(true);
        frame.add(cands);

        select = new JButton("Vote");
        select.addActionListener(this);

        frame.add(select);
    }
    public void voter() {
        if ((!(presresult.equals("(Select Presedential Candidate or type in a write in)"))) || (!(vpresult.equals("(Select Vice Presidential Candidate or type in a write in)"))) || (!(curesult.equals("(Select Cultural Union Candidate or type in a write in)"))) || (!(result.equals("(Select Cultural Union Candidate or type in a write in)")))) {
            if (result.equals(curesult)) {
                JOptionPane.showMessageDialog(frame, "You selected the same candidate for both Cultural Union inputs. You must select two different candidates");
            } else {
                FullBallot ballot = new FullBallot();
                ballot.setPres(presresult);
                ballot.setVp(vpresult);
                ballot.setCu1(curesult);
                ballot.setCu2(result);
                helper.vote(ballot, code);
                JOptionPane.showMessageDialog(frame, "Vote Submitted");
                frame.setVisible(false);
                run();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "You must either vote for somebody or make sure the space is blank");
        }
    }

    public void reset() {
        run();
    }
}
