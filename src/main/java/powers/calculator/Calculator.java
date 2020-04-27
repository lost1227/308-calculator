package powers.calculator;

import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculator {
	
	public enum Operation { RESULT, PLUS, MINUS, TIMES, DIVIDE, SQUARE, POWER, NEGATE}
	
	private double value = 0;
	private Operation state = Operation.RESULT;
	
	private double round(double value, int places) {
		if(places < 0) throw new IllegalArgumentException();
		
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public double calculate(Operation newState, double newValue) {
		
		// binary operators
		switch(state) {
		case PLUS:
			value += newValue;
			break;
		case MINUS:
			value -= newValue;
			break;
		case TIMES:
			value *= newValue;
			break;
		case DIVIDE:
			value /= newValue;
			break;
		case POWER:
			value = Math.pow(value, newValue);
			break;
		case RESULT:
			value = newValue;
		default:
			break;
		}
		
		state = newState;
		
		// unary operators
		switch(newState) {
		case SQUARE:
			value = newValue * newValue;
			state = Operation.RESULT;
			break;
		case NEGATE:
			value = -1 * value;
			state = Operation.RESULT;
			break;
		default:
			break;
		}
		
		if(!Double.isFinite(value)) {
			double ret = value;
			clear();
			return ret;
		}
		
		value = round(value, 15);
		
		return value;
	}
	
	public boolean inBinaryOperation() {
		return state != Operation.RESULT;
	}
	
	public void clear() {
		state = Operation.RESULT;
		value = 0;
	}
}
