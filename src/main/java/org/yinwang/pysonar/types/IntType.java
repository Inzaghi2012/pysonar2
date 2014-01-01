package org.yinwang.pysonar.types;


import org.yinwang.pysonar.Analyzer;

import java.math.BigInteger;


public class IntType extends NumType {

    public BigInteger lower;
    public BigInteger upper;


    public IntType() {
        this.lower = null;
        this.upper = null;
    }


    public IntType(BigInteger value) {
        this.lower = this.upper = value;
    }


    public IntType(BigInteger lower, BigInteger upper) {
        this.lower = lower;
        this.upper = upper;
    }


    public IntType(IntType other) {
        this.lower = other.lower;
        this.upper = other.upper;
    }


    public static IntType add(IntType a, IntType b) {
        BigInteger lower = a.lower == null || b.lower == null ? null : a.lower.add(b.lower);
        BigInteger upper = a.upper == null || b.upper == null ? null : a.upper.add(b.upper);
        return new IntType(lower, upper);
    }


    public static IntType sub(IntType a, IntType b) {
        BigInteger lower = a.lower == null || b.lower == null ? null : a.lower.subtract(b.upper);
        BigInteger upper = a.upper == null || b.upper == null ? null : a.upper.subtract(b.lower);
        return new IntType(lower, upper);
    }


    public IntType negate() {
        BigInteger lower = this.lower == null ? null : this.lower.negate();
        BigInteger upper = this.upper == null ? null : this.upper.negate();
        return new IntType(upper, lower);
    }


    public static IntType mul(IntType a, IntType b) {
        BigInteger lower = a.lower == null || b.lower == null ? null : a.lower.multiply(b.lower);
        BigInteger upper = a.upper == null || b.upper == null ? null : a.upper.multiply(b.upper);
        return new IntType(lower, upper);
    }


    public static IntType div(IntType a, IntType b) {
        BigInteger lower = BigInteger.ZERO;
        if (lower != null && !b.upper.equals(BigInteger.ZERO)) {
            lower = a.lower.divide(b.upper);
        } else {
            lower = null;
        }

        BigInteger upper = BigInteger.ZERO;
        if (upper != null && !b.lower.equals(BigInteger.ZERO)) {
            upper = a.upper.divide(b.lower);
        } else {
            upper = null;
        }

        return new IntType(lower, upper);
    }


    public boolean lt(IntType other) {
        return this.isUpperBounded() &&
                other.isLowerBounded() &&
                this.upper.compareTo(other.lower) < 0;
    }


    public boolean lte(IntType other) {
        return this.isUpperBounded() &&
                other.isLowerBounded() &&
                this.upper.compareTo(other.lower) <= 0;
    }


    public boolean lt(BigInteger other) {
        return this.isUpperBounded() && this.upper.compareTo(other) < 0;
    }


    public boolean lte(BigInteger other) {
        return this.isUpperBounded() && this.upper.compareTo(other) <= 0;
    }


    public boolean gt(IntType other) {
        return this.isLowerBounded() &&
                other.isUpperBounded() &&
                this.lower.compareTo(other.upper) > 0;
    }


    public boolean gte(IntType other) {
        return this.isLowerBounded() &&
                other.isUpperBounded() &&
                this.lower.compareTo(other.upper) >= 0;
    }


    public boolean gt(BigInteger other) {
        return this.isLowerBounded() && this.lower.compareTo(other) > 0;
    }


    public boolean gte(BigInteger other) {
        return this.isLowerBounded() && this.lower.compareTo(other) >= 0;
    }


    public boolean eq(IntType other) {
        return isActualValue() && other.isActualValue() &&
                this.lower.equals(other.lower);
    }


    public boolean isZero() {
        return isActualValue() && lower.equals(BigInteger.ZERO);
    }


    public boolean isUpperBounded() {
        return upper != null;
    }


    public boolean isLowerBounded() {
        return lower != null;
    }


    public boolean isActualValue() {
        return isLowerBounded() && isUpperBounded() && lower.equals(upper);
    }


    public void setLowerInclusive(IntType other) {
        this.lower = other.lower;
    }


    public void setLowerExclusive(IntType other) {
        if (other.lower == null) {
            this.lower = null;
        } else {
            this.lower = other.lower.add(BigInteger.ONE);
        }
    }


    public void setUpperInclusive(IntType other) {
        this.upper = other.upper;
    }


    public void setUpperExclusive(IntType other) {
        if (other.upper == null) {
            this.upper = null;
        } else {
            this.upper = other.upper.subtract(BigInteger.ONE);
        }
    }


    public FloatType toFloatType() {
        double lower = this.lower == null ? Double.NEGATIVE_INFINITY : this.lower.doubleValue();
        double upper = this.upper == null ? Double.POSITIVE_INFINITY : this.upper.doubleValue();
        return new FloatType(lower, upper);
    }


    @Override
    protected String printType(Type.CyclicTypeRecorder ctr) {
        StringBuilder sb = new StringBuilder("int");

        if (Analyzer.self.hasOption("debug")) {
            if (isActualValue() && lower.equals(upper)) {
                sb.append("(" + lower + ")");
            } else if (isLowerBounded() || isUpperBounded()) {
                sb.append("[");
                if (isLowerBounded()) {
                    sb.append(lower);
                } else {
                    sb.append("-∞");
                }
                sb.append("..");
                if (isUpperBounded()) {
                    sb.append(upper);
                } else {
                    sb.append("+∞");
                }
                sb.append("]");
            }
        }

        return sb.toString();
    }

}
