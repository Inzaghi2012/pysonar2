package org.yinwang.pysonar.types;


import org.yinwang.pysonar.Analyzer;

import java.math.BigDecimal;
import java.math.BigInteger;


public class IntType extends NumType {

    public BigInteger lower;
    public BigInteger upper;
    public boolean lowerInclusive;
    public boolean upperInclusive;


    public IntType() {
        this.lower = null;
        this.upper = null;
    }


    public IntType(BigInteger value) {
        this.lower = this.upper = value;
        this.lowerInclusive = this.upperInclusive = true;
    }


    public IntType(BigInteger lower, BigInteger upper) {
        this.lower = lower;
        this.upper = upper;
        this.lowerInclusive = this.upperInclusive = true;
    }


    public IntType(BigInteger lower, BigInteger upper, boolean lowerInclusive, boolean upperInclusive) {
        this.lower = lower;
        this.upper = upper;
        this.lowerInclusive = lowerInclusive;
        this.upperInclusive = upperInclusive;
    }


    public IntType(IntType other) {
        this.lower = other.lower;
        this.upper = other.upper;
        this.lowerInclusive = other.lowerInclusive;
        this.upperInclusive = other.upperInclusive;
    }


    public static IntType add(IntType a, IntType b) {
        BigInteger lower = a.lower == null || b.lower == null ? null : a.lower.add(b.lower);
        BigInteger upper = a.upper == null || b.upper == null ? null : a.upper.add(b.upper);
        boolean lowerInclusive = a.lowerInclusive && b.lowerInclusive;
        boolean upperInclusive = a.upperInclusive && b.upperInclusive;
        return new IntType(lower, upper, lowerInclusive, upperInclusive);
    }


    public static IntType sub(IntType a, IntType b) {
        BigInteger lower = a.lower == null || b.lower == null ? null : a.lower.subtract(b.upper);
        BigInteger upper = a.upper == null || b.upper == null ? null : a.upper.subtract(b.lower);
        boolean lowerInclusive = !b.upperInclusive;
        boolean upperInclusive = !b.lowerInclusive;
        return new IntType(lower, upper, lowerInclusive, upperInclusive);
    }


    public IntType negate() {
        BigInteger lower = this.lower == null ? null : this.lower.negate();
        BigInteger upper = this.upper == null ? null : this.upper.negate();
        return new IntType(lower, upper, upperInclusive, lowerInclusive);
    }


    public static IntType mul(IntType a, IntType b) {
        BigInteger lower = a.lower == null || b.lower == null ? null : a.lower.multiply(b.lower);
        BigInteger upper = a.upper == null || b.upper == null ? null : a.upper.multiply(b.upper);
        boolean lowerInclusive = a.lowerInclusive && b.lowerInclusive;
        boolean upperInclusive = a.upperInclusive && b.upperInclusive;
        return new IntType(lower, upper, lowerInclusive, upperInclusive);
    }


    public static IntType div(IntType a, IntType b) {
        BigInteger lower;
        if (b.upper != null && !b.upper.equals(BigInteger.ZERO)) {
            lower = a.lower.divide(b.upper);
        } else {
            lower = null;
        }

        BigInteger upper;
        if (b.lower != null && !b.lower.equals(BigInteger.ZERO)) {
            upper = a.upper.divide(b.lower);
        } else {
            upper = null;
        }

        boolean lowerInclusive = a.lowerInclusive && b.upperInclusive;
        boolean upperInclusive = a.upperInclusive && b.lowerInclusive;
        return new IntType(lower, upper, lowerInclusive, upperInclusive);
    }


    public boolean lt(IntType other) {
        return upper != null &&
                other.lower != null &&
                (this.upper.compareTo(other.lower) < 0 ||
                        (this.upper.compareTo(other.lower) == 0 && (!this.upperInclusive || !other.lowerInclusive)));
    }


    public boolean lte(IntType other) {
        return upper != null &&
                other.lower != null &&
                this.upper.compareTo(other.lower) <= 0;
    }


    public boolean lt(BigInteger other) {
        return upper != null &&
                (this.upper.compareTo(other) < 0 ||
                        (this.upper.compareTo(other) == 0 && !this.upperInclusive));
    }


    public boolean lte(BigInteger other) {
        return upper != null && this.upper.compareTo(other) <= 0;
    }


    public boolean gt(IntType other) {
        return this.lower != null &&
                other.upper != null &&
                (this.lower.compareTo(other.upper) > 0 ||
                        (this.lower.compareTo(other.upper) == 0 && (!this.lowerInclusive || !other.upperInclusive)));
    }


    public boolean gte(IntType other) {
        return lower != null &&
                other.upper != null &&
                this.lower.compareTo(other.upper) >= 0;
    }


    public boolean gt(BigInteger other) {
        return this.lower != null &&
                (this.lower.compareTo(other) > 0 || (this.lower.compareTo(other) == 0 && !this.lowerInclusive));
    }


    public boolean gte(BigInteger other) {
        return lower != null && this.lower.compareTo(other) >= 0;
    }


    public boolean eq(IntType other) {
        return isActualValue() && other.isActualValue() &&
                this.lower.equals(other.lower);
    }


    public boolean isZero() {
        return isActualValue() && lower.equals(BigInteger.ZERO);
    }


    public boolean isActualValue() {
        return lower != null && upper != null && lower.equals(upper);
    }


    public void setLowerInclusive(IntType other) {
        this.lower = other.lower;
        this.lowerInclusive = true;
    }


    public void setLowerInclusive(FloatType other) {
        if (Math.floor(other.lower) == other.lower) {
            this.lower = BigDecimal.valueOf(other.lower).toBigInteger();
        } else {
            this.lower = BigDecimal.valueOf(Math.ceil(other.lower)).toBigInteger();
        }
        this.lowerInclusive = true;
    }


    public void setLowerExclusive(IntType other) {
        this.lower = other.lower;
        this.lowerInclusive = false;
    }


    public void setLowerExclusive(FloatType other) {
        if (Math.floor(other.lower) == other.lower) {
            this.lower = BigDecimal.valueOf(other.lower).toBigInteger();
            this.lowerInclusive = false;
        } else {
            this.lower = BigDecimal.valueOf(Math.ceil(other.lower)).toBigInteger();
            this.lowerInclusive = true;
        }
    }


    public void setUpperInclusive(IntType other) {
        this.upper = other.upper;
        this.upperInclusive = true;
    }


    public void setUpperInclusive(FloatType other) {
        if (Math.floor(other.upper) == other.upper) {
            this.upper = BigDecimal.valueOf(other.upper).toBigInteger();
        } else {
            this.upper = BigDecimal.valueOf(Math.floor(other.upper)).toBigInteger();
        }
        this.upperInclusive = true;
    }


    public void setUpperExclusive(IntType other) {
        this.upper = other.upper;
        this.upperInclusive = false;
    }


    public void setUpperExclusive(FloatType other) {
        if (Math.floor(other.upper) == other.upper) {
            this.upper = BigDecimal.valueOf(other.upper).toBigInteger();
            this.upperInclusive = false;
        } else {
            this.upper = BigDecimal.valueOf(Math.floor(other.upper)).toBigInteger();
            this.upperInclusive = true;
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
                sb.append("[" + lower + "]");
            } else {
                if (lower != null || upper != null) {
                    if (lowerInclusive) {
                        sb.append("[");
                    } else {
                        sb.append("(");
                    }
                    if (lower != null) {
                        sb.append(lower);
                    } else {
                        sb.append("-∞");
                    }
                    sb.append(", ");
                    if (upper != null) {
                        sb.append(upper);
                    } else {
                        sb.append("+∞");
                    }
                    if (upperInclusive) {
                        sb.append("]");
                    } else {
                        sb.append(")");
                    }
                }
            }
        }

        return sb.toString();
    }

}
