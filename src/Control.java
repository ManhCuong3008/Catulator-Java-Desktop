
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Control implements ActionListener {

    private Main m;
    private boolean process = false;
    private boolean reset = false;
    private int operate = 0;
    private BigDecimal firstNum;
    private BigDecimal secondNum;
    private BigDecimal memory = new BigDecimal("0");

    public Control(Main m) {
        this.m = m;
        pressButton();
    }

    public void pressButton() {
        m.getBtn0().addActionListener(this);
        m.getBtn1().addActionListener(this);
        m.getBtn2().addActionListener(this);
        m.getBtn3().addActionListener(this);
        m.getBtn4().addActionListener(this);
        m.getBtn5().addActionListener(this);
        m.getBtn6().addActionListener(this);
        m.getBtn7().addActionListener(this);
        m.getBtn8().addActionListener(this);
        m.getBtn9().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();//get text in button
        if (process || reset) {// có nghĩa là được nhập số mới
            m.txtScreen.setText("0");
            process = false;
            reset = false;
        }
        String txt = m.txtScreen.getText() + cmd;
        BigDecimal number = new BigDecimal(txt);
        if (number.doubleValue() == 0) {
            if (m.txtScreen.getText().equals("0")) {
                m.txtScreen.setText("0");
            } else {
                m.txtScreen.setText(txt);
            }
        } else {
            m.txtScreen.setText(number.toPlainString() + "");
        }
    }

    public void operator(int operate) {
        calculate();
        this.operate = operate;
        process = true;// xét process về true vì đang ấn vào toán tử    
    }

    public BigDecimal getValue() {
        return new BigDecimal(m.txtScreen.getText());
    }

    public void calculate() {
        try {
            if (!process) {
                if (operate == 0) {
                    firstNum = getValue();
                } else {
                    secondNum = getValue();
                    switch (operate) {
                        case 1://add
                            firstNum = firstNum.add(secondNum);
                            break;
                        case 2://sub
                            firstNum = firstNum.subtract(secondNum);
                            break;
                        case 3://multi
                            firstNum = firstNum.multiply(secondNum);
                            break;
                        case 4://devi
                            if (secondNum.equals(new BigDecimal("0"))) {
                                m.txtScreen.setText("ERROR");
                                return;
                            }
                            firstNum = firstNum.divide(secondNum, 12, RoundingMode.HALF_UP).stripTrailingZeros();
                            break;
                    }
                }
                firstNum = new BigDecimal(firstNum.toPlainString());
                m.txtScreen.setText(firstNum.stripTrailingZeros().toPlainString() + "");
                process = true; // process is set = true to press other operator
            }
        } catch (Exception e) {
            reset = true;
            m.txtScreen.setText("ERROR");
        }
    }

    public void pressResult() {
        // check if any operator wrong
        if (!m.txtScreen.getText().equals("ERROR")) {
            calculate();// call funtion caculate
            process = true;
            operate = 0;
        } else {
            m.txtScreen.setText(firstNum.stripTrailingZeros().toPlainString() + ""); // return value of the first number //???
        }
    }

    public void pressPercen() {
        try {
            if (!m.getTxtScreen().getText().equals("ERROR")) {
                pressResult();
                BigDecimal num = new BigDecimal("100");
                BigDecimal value = getValue().divide(num, 15, RoundingMode.HALF_UP);
                m.txtScreen.setText(value.toPlainString() + "");
                firstNum = getValue();
                process = false; // to click another operator 
                reset = true;
            }
        } catch (Exception e) {
            m.txtScreen.setText("ERROR");
        }
    }

    public void pressClear() {
        process = false;
        operate = 0;
        firstNum = new BigDecimal("0");
        secondNum = new BigDecimal("0");
        m.txtScreen.setText("0");
    }

    public void pressInvert() {
        try {
            if (!m.txtScreen.getText().equals("ERROR")) {
                double value = getValue().doubleValue();
                double result = 0;
                if (value != 0) {
                    result = 1 / value;
                    BigDecimal result1 = new BigDecimal(result);
                    BigDecimal result2 = new BigDecimal("1");
                    result1 = result1.divide(result2, 15, RoundingMode.HALF_UP);
                    m.txtScreen.setText(result1.toPlainString());
                    process = false;
                } else {
                    m.txtScreen.setText("ERROR");
                }
                reset = true;
            }
        } catch (Exception e) {
            m.txtScreen.setText("ERROR");
        }
    }

    public void pressSqrt() {
        try {
            if (!m.txtScreen.getText().equals("ERROR")) {
                pressResult();
                BigDecimal result = getValue();
                if (result.doubleValue() >= 0) { // number to sqrt must be >= 0
                    String value = Math.sqrt(result.doubleValue()) + "";
                    if (value.endsWith(".0")) {
                        value = value.replace(".0", "");
                    }
                    m.txtScreen.setText(new BigDecimal(value).stripTrailingZeros().toPlainString());
                    process = false;
                } else {
                    m.txtScreen.setText("ERROR");
                }
            }
        } catch (Exception e) {
            m.txtScreen.setText("ERROR");
        }
        reset = true; // user can press another number
    }

    public void pressSwap() {
        try {
            if (!m.getTxtScreen().getText().equals("ERROR")) {
                if (m.txtScreen.getText().startsWith("-")) {
                    m.txtScreen.setText(m.txtScreen.getText().substring(1, m.txtScreen.getText().length()));
                    firstNum = getValue();
                } else {
                    m.txtScreen.setText("-" + m.txtScreen.getText());
                    firstNum = getValue();
                }
                process = false; // process set to false to do any operator
            }
        } catch (Exception e) {
            m.txtScreen.setText("ERROR");
        }
        reset = true;
    }

    public void pressDot() {
        if (process == true || reset == true) { // nhập 1 số mới 
            m.txtScreen.setText("0.");
            process = false;
            reset = false;
            //status = false;
        }
        if (!m.txtScreen.getText().contains(".")) {
            m.txtScreen.setText(m.txtScreen.getText() + ".");
        }
    }

    //Nhấn M+ / M- => MR đổi màu
    //Nhấn MC: => MR về màu cũ
    public void pressMAdd() {
        try {
            memory = memory.add(getValue());
            process = false;
        } catch (Exception e) {
            m.txtScreen.setText("ERROR");
        }
        reset = true;
    }

    public void pressMSub() {
        try {
            memory = memory.subtract(getValue());
            process = false;
        } catch (Exception e) {
            m.txtScreen.setText("ERROR");
        }
        reset = true;
    }

    public void pressMR() {
        m.txtScreen.setText(memory + "");
        process = false;
        reset = true;
    }

    public void pressMClear() {
        memory = new BigDecimal("0");
        process = false;
        reset = true;
    }

    public void removeLast() {
        if (!reset) {
            if (!process) {
                String txt = m.txtScreen.getText();
                String result = "0";
                if (txt.length() > 1) {
                    result = txt.substring(0, txt.length() - 1);
                }
                if (txt.length() == 1) {
                    result = "0";
                } else if (txt.startsWith("-") && txt.length() == 1) {
                    result = "0";
                }
                m.txtScreen.setText(result);
            }
        }
    }

}
