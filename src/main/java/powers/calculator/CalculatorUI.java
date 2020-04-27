package powers.calculator;


import java.util.function.UnaryOperator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

public class CalculatorUI extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private static final int BUTTON_SIZE = 60;
	
	private TextField input;
	private TextFormatter<String> formatter;
	
	private Label stateLabel;
	
	private Calculator calc = new Calculator();
	
	private boolean buildingInput = true;

	private void setupNextInput() {
		input.setText("");
		buildingInput = true;
	}
	
	private void displayResult(double result) {
		input.setText(formatNumber(result));
		buildingInput = false;
	}
	
	private void refocus() {
		input.requestFocus();
		int len = input.getText().length();
		input.selectRange(len, len);
	}
	
	private void clear() {
		calc.clear();
		setupNextInput();
		stateLabel.setText("");
		refocus();
	}
	
	private void pushButton(String buttonStr) {
		if(!buildingInput) {
			clear();
		}
		input.setText(input.getText() + buttonStr);
		refocus();
	}
	
	String formatNumber(double value) {
		if(Math.floor(value) == value) {
			return String.format("%.0f", value);
		} else {
			return Double.toString(value);
		}
	}
	
	private void action(Calculator.Operation newstate) {
		input.commitValue();
		String newValueStr = formatter.getValue();
		
		double newValue;
		
		try {
			newValue = Double.valueOf(newValueStr);
		} catch(NumberFormatException e) {
			if(newValueStr.isEmpty()) {
				newValue = 0;
			} else {
				stateLabel.setText("Error");
				calc.clear();
				setupNextInput();
				refocus();
				return;
			}
		}
		
		double value = calc.calculate(newstate, newValue);
		
		switch(newstate) {
		case PLUS:
			stateLabel.setText(String.format("%s +", formatNumber(value)));
			setupNextInput();
			break;
		case MINUS:
			stateLabel.setText(String.format("%s -", formatNumber(value)));
			setupNextInput();
			break;
		case TIMES:
			stateLabel.setText(String.format("%s *", formatNumber(value)));
			setupNextInput();
			break;
		case DIVIDE:
			stateLabel.setText(String.format("%s /", formatNumber(value)));
			setupNextInput();
			break;
		case POWER:
			stateLabel.setText(String.format("%s ^", formatNumber(value)));
			setupNextInput();
			break;
		case NEGATE:
		case SQUARE:
		case LOG:
		case RESULT:
			if(Double.isFinite(value)) {
				stateLabel.setText("");
				displayResult(value);
			} else {
				stateLabel.setText("Error");
				calc.clear();
				setupNextInput();
			}
		}
		
		refocus();
	}
	
	private boolean minusPushed = false;

	@Override
	public void start(Stage stage) {
		GridPane grid = new GridPane();
		grid.setHgap(0);
		grid.setVgap(0);
		
		// grid.gridLinesVisibleProperty().set(true);
		
		UnaryOperator<Change> doubleFilter = change -> {
			String newText = change.getControlNewText();
			if(minusPushed && change.getText().equals("-")) {
				minusPushed = false;
				return null;
			}
			if(newText.matches("-?[0-9]*\\.?[0-9]*")) {
				return change;
			}
			return null;
		};
		
		input = new TextField();
		formatter =  new TextFormatter<String>(
						new DefaultStringConverter(), "", doubleFilter
				);
		input.setTextFormatter(formatter);
		setupNextInput();
		
		input.setOnKeyPressed((e) -> {
			switch(e.getCode()) {
			case ENTER:
				action(Calculator.Operation.RESULT);
				break;
			case ADD:
			case PLUS:
				action(Calculator.Operation.PLUS);
				break;
			case SUBTRACT:
			case MINUS:
				minusPushed = true;
				action(Calculator.Operation.MINUS);
				break;
			case MULTIPLY:
			case STAR:
				action(Calculator.Operation.TIMES);
				break;
			case DIVIDE:
			case SLASH:
				action(Calculator.Operation.DIVIDE);
				break;
			default:
				if(!buildingInput) {
					clear();
				}
				buildingInput = true;
				break;
			}
		});
		
		refocus();
		grid.add(input, 0, 1, 4, 1);
		
		stateLabel = new Label("");
		grid.add(stateLabel, 0, 0, 4, 1);
		
		Button log = new Button("log(x)");
		log.setPrefWidth(BUTTON_SIZE);
		log.setPrefHeight(BUTTON_SIZE);
		log.setOnAction((e) -> action(Calculator.Operation.LOG));
		grid.add(log, 0, 2);
		
		Button power = new Button("x^y");
		power.setPrefWidth(BUTTON_SIZE);
		power.setPrefHeight(BUTTON_SIZE);
		power.setOnAction((e) -> action(Calculator.Operation.POWER));
		grid.add(power, 1, 2);
		
		Button clear = new Button("CE");
		clear.setPrefWidth(BUTTON_SIZE);
		clear.setPrefHeight(BUTTON_SIZE);
		clear.setOnAction((e)->clear());
		grid.add(clear,  2,  2);
		
		for(int i = 1; i <= 9; i++) {
			final String istr = Integer.toString(i);
			Button button = new Button(istr);
			button.setPrefWidth(BUTTON_SIZE);
			button.setPrefHeight(BUTTON_SIZE);
			button.setOnAction((e) -> pushButton(istr));
			
			grid.add(button, (i - 1) % 3, 5 - ((i - 1) / 3));
		}
		
		Button zero = new Button("0");
		zero.setPrefWidth(BUTTON_SIZE);
		zero.setPrefHeight(BUTTON_SIZE);
		zero.setOnAction((e) -> pushButton("0"));
		grid.add(zero, 1, 6);
		
		Button decimal = new Button(".");
		decimal.setPrefWidth(BUTTON_SIZE);
		decimal.setPrefHeight(BUTTON_SIZE);
		decimal.setOnAction((e) -> pushButton("."));
		grid.add(decimal, 2, 6);
		
		Button negate = new Button("+/-");
		negate.setPrefWidth(BUTTON_SIZE);
		negate.setPrefHeight(BUTTON_SIZE);
		negate.setOnAction((e) -> action(Calculator.Operation.NEGATE));
		grid.add(negate, 0, 6);
		
		
		Button divide = new Button("/");
		divide.setPrefWidth(BUTTON_SIZE);
		divide.setPrefHeight(BUTTON_SIZE);
		divide.setOnAction((e) -> action(Calculator.Operation.DIVIDE));
		grid.add(divide, 3, 2);
		
		Button times = new Button("*");
		times.setPrefWidth(BUTTON_SIZE);
		times.setPrefHeight(BUTTON_SIZE);
		times.setOnAction((e) -> action(Calculator.Operation.TIMES));
		grid.add(times, 3, 3);
		
		Button minus = new Button("-");
		minus.setPrefWidth(BUTTON_SIZE);
		minus.setPrefHeight(BUTTON_SIZE);
		minus.setOnAction((e) -> action(Calculator.Operation.MINUS));
		grid.add(minus, 3, 4);
		
		Button plus = new Button("+");
		plus.setPrefWidth(BUTTON_SIZE);
		plus.setPrefHeight(BUTTON_SIZE);
		plus.setOnAction((e) -> action(Calculator.Operation.PLUS));
		grid.add(plus, 3, 5);
		
		Button equal = new Button("=");
		equal.setPrefWidth(BUTTON_SIZE);
		equal.setPrefHeight(BUTTON_SIZE);
		equal.setOnAction((e) -> action(Calculator.Operation.RESULT));
		grid.add(equal, 3, 6);
		
		stage.setTitle("Calculator");
		Scene scene = new Scene(grid);
		stage.setScene(scene);
		
		stage.show();
	}
}
