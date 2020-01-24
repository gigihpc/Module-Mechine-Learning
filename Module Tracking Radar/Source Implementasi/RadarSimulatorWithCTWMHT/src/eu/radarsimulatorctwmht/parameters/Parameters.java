/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorctwmht.parameters;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author GIH
 */
public class Parameters {
    public static final String PARAMETER_ADDED_PROPERTY = "param_added";
    private static String context = "";
    private JFrame frame = null;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private List<Group> groups = new ArrayList<Group>();
    private static final Logger logger = Logger.getLogger(Parameters.class);
    private Map<ParameterKey, Parameter> parameters = new HashMap<ParameterKey, Parameter>();
    public static final Parameters instance = new Parameters();

    private Parameters() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane);
    }

    public class Group {

        private String name;
        private JPanel panel;

        Group(String name) {
            this.name = name;
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
            tabbedPane.add(name, panel);
        }

        public String getName() {
            return name;
        }

        public JPanel getPanel() {
            return panel;
        }
    }

    public Group newGroup(String name) {
        Group g = new Group(name);
        groups.add(g);
        return g;
    }

    public static interface ParameterChangeListener<T> {

        public void parameterChanged(T val);
    }

    static class ParameterKey implements Serializable {

        String name;

        public ParameterKey(Class c, String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ParameterKey other = (ParameterKey) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return "ParameterKey{" + "name=" + name + '}';
        }
    }

    interface Parameter {

        public String getName();

        public Object getValue();

        public void setValueFromString(String value);

        public void setValue(Object value);
    }

    static class IntParameter implements Parameter, Serializable {

        Class c;
        String name;
        String labelText;
        JLabel label;
        JSlider slider;
        int min;
        int max;

        public IntParameter(Class c, String name, String labelText, JLabel label, JSlider slider, int min, int max) {
            this.c = c;
            this.name = name;
            this.labelText = labelText;
            this.label = label;
            this.slider = slider;
            this.min = min;
            this.max = max;
        }

        public Integer getValue() {
            return slider.getValue();
        }

        public void setValue(Object value) {
            slider.setValue((Integer) value);
        }

        public String getName() {
            return name;
        }

        public void setValueFromString(String value) {
            setValue(Integer.parseInt(value));
        }
    }

    static class CollectionParameter implements Parameter, Serializable {

        Class c;
        String name;
        String labelText;
        JLabel label;
        JComboBox comboBox;

        public CollectionParameter(Class c, String name, String labelText, JLabel label, JComboBox comboBox) {
            this.c = c;
            this.name = name;
            this.labelText = labelText;
            this.label = label;
            this.comboBox = comboBox;
        }

        public Object getValue() {
            return comboBox.getSelectedItem();
        }

        public void setValue(Object value) {
            comboBox.setSelectedItem(value);
        }

        public String getName() {
            return name;
        }

        public void setValueFromString(String value) {
            throw new UnsupportedOperationException("Cannot read collections from strings.");
        }
    }

    static class StringParameter implements Parameter, Serializable {

        Class c;
        String name;
        String labelText;
        JLabel label;
        JTextField textField;

        public StringParameter(Class c, String name, String labelText, JLabel label, JTextField textField) {
            this.c = c;
            this.name = name;
            this.labelText = labelText;
            this.label = label;
            this.textField = textField;
        }

        public String getValue() {
            return textField.getText();
        }

        public void setValue(Object value) {
            textField.setText((String) value);
        }

        public String getName() {
            return name;
        }

        public void setValueFromString(String value) {
            setValue(value);
        }
    }

    static class DoubleParameter implements Parameter, Serializable {

        private static final int PRECISION = 1000;
        Class c;
        String name;
        String labelText;
        JLabel label;
        JSlider slider;
        double min;
        double max;

        public DoubleParameter(Class c, String name, String labelText, JLabel label, double min, double max) {
            this.c = c;
            this.name = name;
            this.labelText = labelText;
            this.label = label;
            this.slider = new JSlider(0, PRECISION);
            this.min = min;
            this.max = max;
        }

        public Double getValue() {
            return min + ((double) slider.getValue() / PRECISION) * (max - min);
        }

        public void setValue(Object value) {
            slider.setValue((int) ((((Double) value - min) / (max - min)) * PRECISION));
        }

        public String getName() {
            return name;
        }

        public void setValueFromString(String value) {
            setValue(Double.parseDouble(value));
        }
    }

    static class BooleanParameter implements Parameter, Serializable {

        Class c;
        String name;
        String labelText;
        JCheckBox checkBox;

        public BooleanParameter(Class c, String name, String labelText) {
            this.c = c;
            this.name = name;
            this.labelText = labelText;
            this.checkBox = new JCheckBox(labelText);
            checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
            checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        public Boolean getValue() {
            return checkBox.isSelected();
        }

        public void setValue(Object value) {
            checkBox.setSelected((Boolean) value);
        }

        public String getName() {
            return name;
        }

        public void setValueFromString(String value) {
            setValue(Boolean.parseBoolean(value));
        }
    }

    private void register(Parameter p) {
        try {
            PrintStream ps;
            dumpTo(ps = new PrintStream("parametersDump.txt"));
            ps.flush();
            ps.close();
        } catch (IOException ex) {
            logger.error(ex + " at " + ex.getStackTrace()[0].toString());
        }
    }

    private void set(Parameter p) {
        try {
            PrintStream ps;
            dumpTo(ps = new PrintStream("parametersDump.txt"));
            ps.flush();
            ps.close();
        } catch (IOException ex) {
            logger.error(ex + " at " + ex.getStackTrace()[0].toString());
        }
    }

    public void registerInt(
            final Group group,
            final Class c,
            final String name,
            final String labelText,
            final int min,
            final int max,
            final ParameterChangeListener<Integer> listener) {

        final IntParameter parameter = new IntParameter(c, name, labelText, new JLabel(labelText), new JSlider(min, max), min, max);
        parameters.put(new ParameterKey(c, name), parameter);
        parameter.slider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                try {
                    Preferences.userNodeForPackage(Parameters.class).putInt("" + (context + c.getName() + "." + name + ".value").hashCode(), parameter.slider.getValue());
                    Preferences.userNodeForPackage(Parameters.class).flush();
                } catch (Exception ex) {
                    logger.error(ex + " at " + ex.getStackTrace()[0].toString());
                }

                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        parameter.label.setText(labelText.replaceAll("\\$VAL", "" + parameter.slider.getValue()));
                    }
                });
                listener.parameterChanged(parameter.getValue());
                set(parameter);
            }
        });
        parameter.slider.setValue(Preferences.userNodeForPackage(Parameters.class).getInt("" + (context + c.getName() + "." + name + ".value").hashCode(), parameter.slider.getValue()));
        for (int i = 0; i < parameter.slider.getChangeListeners().length; i++) {
            ChangeListener changeListener = parameter.slider.getChangeListeners()[i];
            changeListener.stateChanged(null);
        }
        group.getPanel().add(parameter.label);
        group.getPanel().add(parameter.slider);
        for (int i = 0; i < group.getPanel().getPropertyChangeListeners().length; i++) {
            PropertyChangeListener propertyChangeListener = group.getPanel().getPropertyChangeListeners()[i];
            propertyChangeListener.propertyChange(null);
        }
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                if (frame != null) {
                    frame.validate();
                }
            }
        });
        register(parameter);
    }

    public void registerString(
            final Group group,
            final Class c,
            final String name,
            final String labelText,
            final ParameterChangeListener<String> listener) {
        final StringParameter parameter = new StringParameter(
                c,
                name,
                labelText,
                new JLabel(labelText),
                new JTextField());

        parameters.put(new ParameterKey(c, name), parameter);

        parameter.textField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Preferences.userNodeForPackage(Parameters.class).put("" + (context + c.getName() + "." + name + ".value").hashCode(), parameter.textField.getText());
                try {
                    Preferences.userNodeForPackage(Parameters.class).flush();
                } catch (BackingStoreException ex) {
                    logger.error(ex + " at " + ex.getStackTrace()[0].toString());
                }
                parameter.label.setText(labelText.replaceAll("\\$VAL", "" + parameter.textField.getText()));
                listener.parameterChanged(parameter.getValue());
                set(parameter);
            }
        });

        parameter.textField.setText(
                Preferences.userNodeForPackage(Parameters.class).get("" + (context + c.getName() + "." + name + ".value").hashCode(), parameter.textField.getText()));
        for (int i = 0; i < parameter.textField.getActionListeners().length; i++) {
            ActionListener actionListener = parameter.textField.getActionListeners()[i];
            actionListener.actionPerformed(null);
        }

        group.getPanel().add(parameter.label);
        group.getPanel().add(parameter.textField);

        for (int i = 0; i < group.getPanel().getPropertyChangeListeners().length; i++) {
            PropertyChangeListener propertyChangeListener = group.getPanel().getPropertyChangeListeners()[i];
            propertyChangeListener.propertyChange(null);
        }
        if (frame != null) {
            frame.pack();
        }
        register(parameter);
    }

    public void registerCollection(
            final Group group,
            final Class c,
            final String name,
            final String labelText,
            final Object[] array,
            final ParameterChangeListener<Object> listener) {
        ArrayList arrayList = new ArrayList();

        for (int i = 0; i < array.length; i++) {
            Object object = array[i];
            arrayList.add(object);
        }

        registerCollection(group, c, name, labelText, arrayList, listener);
        if (frame != null) {
            frame.pack();
        }
    }

    public void registerCollection(
            final Group group,
            final Class c,
            final String name,
            final String labelText,
            final Collection collection,
            final ParameterChangeListener<Object> listener) {
        final CollectionParameter parameter = new CollectionParameter(
                c,
                name,
                labelText,
                new JLabel(labelText),
                new JComboBox(collection.toArray()));

        parameters.put(new ParameterKey(c, name), parameter);

        parameter.comboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Preferences.userNodeForPackage(Parameters.class).putInt("" + (context + c.getName() + "." + name + ".value").hashCode(), parameter.comboBox.getSelectedIndex());
                try {
                    Preferences.userNodeForPackage(Parameters.class).flush();
                } catch (BackingStoreException ex) {
                    logger.error(ex + " at " + ex.getStackTrace()[0].toString());
                }
                parameter.label.setText(labelText.replaceAll("\\$VAL", "" + parameter.comboBox.getSelectedItem()));
                listener.parameterChanged(parameter.getValue());
                set(parameter);
            }
        });

        parameter.comboBox.setSelectedIndex(
                Preferences.userNodeForPackage(Parameters.class).getInt("" + (context + c.getName() + "." + name + ".value").hashCode(), 0));
        for (int i = 0; i < parameter.comboBox.getActionListeners().length; i++) {
            ActionListener actionListener = parameter.comboBox.getActionListeners()[i];
            actionListener.actionPerformed(null);
        }

        group.getPanel().add(parameter.label);
        group.getPanel().add(parameter.comboBox);

        for (int i = 0; i < group.getPanel().getPropertyChangeListeners().length; i++) {
            PropertyChangeListener propertyChangeListener = group.getPanel().getPropertyChangeListeners()[i];
            propertyChangeListener.propertyChange(null);
        }
        if (frame != null) {
            frame.pack();
        }
        register(parameter);
    }

    public void registerDouble(
            final Group group,
            final Class c,
            final String name,
            final String labelText,
            final double min,
            final double max,
            final ParameterChangeListener<Double> listener) {
        final DoubleParameter parameter = new DoubleParameter(
                c,
                name,
                labelText,
                new JLabel(labelText),
                min,
                max);

        parameters.put(new ParameterKey(c, name), parameter);

        parameter.slider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                Preferences.userNodeForPackage(Parameters.class).putInt("" + (context + c.getName() + "." + name + ".value").hashCode(), parameter.slider.getValue());
                try {
                    Preferences.userNodeForPackage(Parameters.class).flush();
                } catch (BackingStoreException ex) {
                    logger.error(ex + " at " + ex.getStackTrace()[0].toString());
                }
                parameter.label.setText(labelText.replaceAll("\\$VAL", "" + parameter.getValue()));
                listener.parameterChanged(parameter.getValue());
                set(parameter);
            }
        });

        parameter.slider.setValue(
                Preferences.userNodeForPackage(Parameters.class).getInt("" + (context + c.getName() + "." + name + ".value").hashCode(), parameter.slider.getValue()));
        for (int i = 0; i < parameter.slider.getChangeListeners().length; i++) {
            ChangeListener changeListener = parameter.slider.getChangeListeners()[i];
            changeListener.stateChanged(null);
        }

        group.getPanel().add(parameter.label);
        group.getPanel().add(parameter.slider);

        for (int i = 0; i < group.getPanel().getPropertyChangeListeners().length; i++) {
            PropertyChangeListener propertyChangeListener = group.getPanel().getPropertyChangeListeners()[i];
            propertyChangeListener.propertyChange(null);
        }
        if (frame != null) {
            frame.pack();
        }
        register(parameter);
    }

    public void registerBoolean(
            final Group group,
            final Class c,
            final String name,
            final String labelText,
            final ParameterChangeListener<Boolean> listener) {
        final BooleanParameter parameter = new BooleanParameter(
                c,
                name,
                labelText);

        parameters.put(new ParameterKey(c, name), parameter);

        parameter.checkBox.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                Preferences.userNodeForPackage(Parameters.class).putBoolean("" + (context + c.getName() + "." + name + ".value").hashCode(), parameter.getValue());
                try {
                    Preferences.userNodeForPackage(Parameters.class).flush();
                } catch (BackingStoreException ex) {
                    logger.error(ex + " at " + ex.getStackTrace()[0].toString());
                }
                parameter.checkBox.setText(labelText.replaceAll("\\$VAL", "" + parameter.getValue()));
                listener.parameterChanged(parameter.getValue());
                set(parameter);
            }
        });

        parameter.checkBox.setSelected(
                Preferences.userNodeForPackage(Parameters.class).getBoolean("" + (context + c.getName() + "." + name + ".value").hashCode(), parameter.getValue()));
        for (int i = 0; i < parameter.checkBox.getChangeListeners().length; i++) {
            ChangeListener changeListener = parameter.checkBox.getChangeListeners()[i];
            changeListener.stateChanged(new ChangeEvent(parameter.checkBox));
        }

        group.getPanel().add(parameter.checkBox);

        for (int i = 0; i < group.getPanel().getPropertyChangeListeners().length; i++) {
            PropertyChangeListener propertyChangeListener = group.getPanel().getPropertyChangeListeners()[i];
            propertyChangeListener.propertyChange(null);
        }
        if (frame != null) {
            frame.pack();
        }
        register(parameter);
    }

    public <T> T get(Class c, String name) {
        if (!parameters.containsKey(new ParameterKey(c, name))) {
            throw new RuntimeException("No such parameter: " + c.getName() + " - " + name);
        }
        return (T) parameters.get(new ParameterKey(c, name)).getValue();
    }

    public void set(Class c, String name, Object val) {
        parameters.get(new ParameterKey(c, name)).setValue(val);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JFrame showFrame() {
        frame = new JFrame("Parameters WITH CTW");
        frame.getContentPane().add(new JScrollPane(getPanel()));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        return frame;
    }

    public static void setContext(String context) {
        Parameters.context = context;
    }

    public String getParameters() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(parameters);
            BASE64Encoder encoder = new BASE64Encoder();
            String encoded = encoder.encode(byteArrayOutputStream.toByteArray());
            return encoded;
        } catch (IOException ex) {
            logger.error(ex + " at " + ex.getStackTrace()[0].toString());
        }
        return "";
    }

    public void setParameters(String param) {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byteArrayInputStream = new ByteArrayInputStream(decoder.decodeBuffer(param));
            ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
            Map<ParameterKey, Parameter> paramMap = (Map<ParameterKey, Parameter>) ois.readObject();

            for (Map.Entry<ParameterKey, Parameter> entry : paramMap.entrySet()) {
                if (parameters.containsKey(entry.getKey())) {
                    parameters.get(entry.getKey()).setValue(entry.getValue().getValue());
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.error(ex + " at " + ex.getStackTrace()[0].toString());
        } catch (IOException ex) {
            logger.error(ex + " at " + ex.getStackTrace()[0].toString());
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException ex) {
                logger.error(ex + " at " + ex.getStackTrace()[0].toString());
            }
        }
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void dumpTo(PrintStream os) {
        for (Parameter parameter : parameters.values()) {
            os.println(parameter.getName() + "=" + parameter.getValue());
        }
    }

    public void readFrom(URL url) throws FileNotFoundException, IOException, ClassNotFoundException {
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        while ((line = br.readLine()) != null) {
            String name = line.split("=")[0];
            String value = line.split("=")[1];
            Parameter p = parameters.get(new ParameterKey(null, name));
            if (p != null) {
                p.setValueFromString(value);
            } else {
                logger.warn("Non existing parameter: " + name);
            }
        }
    }

    public void tryLoadDefaultParameters() {
        try {
            boolean loadedOnce = Preferences.userNodeForPackage(Parameters.class).getBoolean("LoadedOnce", false);
            if (!loadedOnce) {
                System.out.println("Loading default parameters");
                readFrom(this.getClass().getClassLoader().getResource("defaultparameters"));
                Preferences.userNodeForPackage(Parameters.class).putBoolean("LoadedOnce", true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex + " at " + ex.getStackTrace()[0].toString());
        }
    }
}
