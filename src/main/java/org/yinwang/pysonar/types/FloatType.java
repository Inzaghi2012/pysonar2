package org.yinwang.pysonar.types;


import org.yinwang.pysonar.Analyzer;


public class FloatType extends NumType {
    public double upper = Double.POSITIVE_INFINITY;
    public double lower = Double.NEGATIVE_INFINITY;
    public boolean lowerInclusive;
    public boolean upperInclusive;


    public FloatType() {
    }


    public FloatType(double value) {
        this.lower = this.upper = value;
        this.lowerInclusive = this.upperInclusive = true;
    }


    public FloatType(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
        this.lowerInclusive = this.upperInclusive = true;
    }


    public FloatType(double lower, double upper, boolean lowerInclusive, boolean upperInclusive) {
        this.lower = lower;
        this.upper = upper;
        this.lowerInclusive = lowerInclusive;
        this.upperInclusive = upperInclusive;
    }


    public FloatType(FloatType other) {
        this.lower = other.lower;
        this.upper = other.upper;
        this.lowerInclusive = other.lowerInclusive;
        this.upperInclusive = other.upperInclusive;
    }


    public static FloatType add(FloatType a, FloatType b) {
        double lower = a.lower + b.lower;
        double upper = a.upper + b.upper;
        boolean lowerInclusive = a.lowerInclusive && b.lowerInclusive;
        boolean upperInclusive = a.upperInclusive && b.upperInclusive;
        return new FloatType(lower, upper, lowerInclusive, upperInclusive);
    }


    public static FloatType sub(FloatType a, FloatType b) {
        double lower = a.lower - b.upper;
        double upper = a.upper - b.lower;
        boolean lowerInclusive = !b.upperInclusive;
        boolean upperInclusive = !b.lowerInclusive;
        return new FloatType(lower, upper, lowerInclusive, upperInclusive);
    }


    public FloatType negate() {
        return new FloatType(-upper, -lower, this.upperInclusive, this.upperInclusive);
    }


    public static FloatType mul(FloatType a, FloatType b) {
        double lower = a.lower * b.lower;
        double upper = a.upper * b.upper;
        boolean lowerInclusive = a.lowerInclusive && b.lowerInclusive;
        boolean upperInclusive = a.upperInclusive && b.upperInclusive;
        return new FloatType(lower, upper, lowerInclusive, upperInclusive);
    }


    public static FloatType div(FloatType a, FloatType b) {
        double lower = a.lower / b.upper;
        double upper = a.upper / b.lower;
        boolean lowerInclusive = a.lowerInclusive && b.upperInclusive;
        boolean upperInclusive = a.upperInclusive && b.lowerInclusive;
        return new FloatType(lower, upper, lowerInclusive, upperInclusive);
    }


    public boolean lt(FloatType other) {
        return (this.upper < other.lower ||
                this.upper == other.lower && (!this.upperInclusive || !other.lowerInclusive));
    }


    public boolean lt(double other) {
        return (this.upper < other ||
                this.upper == other && !this.upperInclusive);
    }


    public boolean lte(FloatType other) {
        return this.upper <= other.lower;
    }


    public boolean lte(double other) {
        return this.upper <= other;
    }


    public boolean gt(FloatType other) {
        return (this.lower > other.upper ||
                this.lower == other.upper && (!this.lowerInclusive || !other.upperInclusive));
    }


    public boolean gt(double other) {
        return (this.lower > other ||
                this.lower == other && (!this.lowerInclusive));
    }


    public boolean gte(FloatType other) {
        return this.lower >= other.upper;
    }


    public boolean gte(double other) {
        return this.lower >= other;
    }


    public boolean eq(FloatType other) {
        return isActualValue() && other.isActualValue() && this.lower == other.lower;
    }


    public boolean isZero() {
        return isActualValue() && lower == 0;
    }


    public boolean isUpperBounded() {
        return upper != Double.POSITIVE_INFINITY;
    }


    public boolean isLowerBounded() {
        return lower != Double.NEGATIVE_INFINITY;
    }


    public boolean isActualValue() {
        return lower == upper;
    }


    public void setUpperInclusive(FloatType other) {
        this.upper = other.upper;
        this.upperInclusive = true;
    }


    public void setUpperInclusive(IntType other) {
        this.upper = other.upper.doubleValue();
        this.upperInclusive = true;
    }


    public void setLowerInclusive(FloatType other) {
        this.lower = other.lower;
        this.lowerInclusive = true;
    }


    public void setLowerInclusive(IntType other) {
        this.lower = other.lower.doubleValue();
        this.lowerInclusive = true;
    }


    public void setUpperExclusive(FloatType other) {
        this.upper = other.upper;
        this.upperInclusive = false;
    }


    public void setUpperExclusive(IntType other) {
        this.upper = other.upper.doubleValue();
        this.upperInclusive = false;
    }


    public void setLowerExclusive(FloatType other) {
        this.lower = other.lower;
        this.lowerInclusive = false;
    }


    public void setLowerExclusive(IntType other) {
        this.lower = other.lower.doubleValue();
        this.lowerInclusive = false;
    }


    public boolean isFeasible() {
        if (lower < upper) {
            return true;
        }
        if (lower == upper && lowerInclusive && upperInclusive) {
            return true;
        }
        return false;
    }


    @Override
    protected String printType(CyclicTypeRecorder ctr) {
        StringBuilder sb = new StringBuilder("float");

        if (Analyzer.self.hasOption("debug")) {
            if (lower == upper) {
                sb.append("[" + lower + "]");
            } else if (isLowerBounded() || isUpperBounded()) {
                if (lowerInclusive && isLowerBounded()) {
                    sb.append("[");
                } else {
                    sb.append("(");
                }

                if (isLowerBounded()) {
                    sb.append(lower);
                } else {
                    sb.append("-∞");
                }
                sb.append(", ");
                if (isUpperBounded()) {
                    sb.append(upper);
                } else {
                    sb.append("+∞");
                }

                if (upperInclusive && isUpperBounded()) {
                    sb.append("]");
                } else {
                    sb.append(")");
                }
            }
        }

        return sb.toString();
    }

}
