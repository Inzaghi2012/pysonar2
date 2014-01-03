package org.yinwang.pysonar.types;


import org.yinwang.pysonar._;

abstract public class NumType extends Type {


    public static NumType copy(NumType a) {
        if (a instanceof IntType) {
            return new IntType((IntType) a);
        }

        if (a instanceof FloatType) {
            return new FloatType((FloatType) a);
        }

        _.die("only accept IntType of FloatType, but got: " + a);
        return null;
    }


    public static NumType add(NumType a, NumType b) {
        if (a instanceof FloatType || b instanceof FloatType) {
            FloatType af = null;
            FloatType bf = null;

            if (a instanceof IntType) {
                af = ((IntType) a).toFloatType();
            } else {
                af = (FloatType) a;
            }

            if (b instanceof IntType) {
                bf = ((IntType) b).toFloatType();
            } else {
                bf = (FloatType) b;
            }

            return FloatType.add(af, bf);
        } else {
            return IntType.add((IntType) a, (IntType) b);
        }
    }


    public static NumType sub(NumType a, NumType b) {
        if (a instanceof FloatType || b instanceof FloatType) {
            FloatType af = null;
            FloatType bf = null;

            if (a instanceof IntType) {
                af = ((IntType) a).toFloatType();
            } else {
                af = (FloatType) a;
            }

            if (b instanceof IntType) {
                bf = ((IntType) b).toFloatType();
            } else {
                bf = (FloatType) b;
            }

            return FloatType.sub(af, bf);
        } else {
            return IntType.sub((IntType) a, (IntType) b);
        }
    }


    public static NumType mul(NumType a, NumType b) {
        if (a instanceof FloatType || b instanceof FloatType) {
            FloatType af = null;
            FloatType bf = null;

            if (a instanceof IntType) {
                af = ((IntType) a).toFloatType();
            } else {
                af = (FloatType) a;
            }

            if (b instanceof IntType) {
                bf = ((IntType) b).toFloatType();
            } else {
                bf = (FloatType) b;
            }

            return FloatType.mul(af, bf);
        } else {
            return IntType.mul((IntType) a, (IntType) b);
        }
    }


    public static NumType div(NumType a, NumType b) {
        if (a instanceof FloatType || b instanceof FloatType) {
            FloatType af = null;
            FloatType bf = null;

            if (a instanceof IntType) {
                af = ((IntType) a).toFloatType();
            } else {
                af = (FloatType) a;
            }

            if (b instanceof IntType) {
                bf = ((IntType) b).toFloatType();
            } else {
                bf = (FloatType) b;
            }

            return FloatType.div(af, bf);
        } else {
            return IntType.div((IntType) a, (IntType) b);
        }
    }


    public static NumType negate(NumType a) {
        if (a instanceof FloatType) {
            return ((FloatType) a).negate();
        } else {
            return ((IntType) a).negate();
        }
    }


    public static boolean neq(NumType a, NumType b) {
        return NumType.lt(a, b) || NumType.gt(a, b);
    }


    public static boolean lt(NumType a, NumType b) {
        if (a instanceof FloatType || b instanceof FloatType) {
            FloatType af;
            FloatType bf;

            if (a instanceof IntType) {
                af = ((IntType) a).toFloatType();
            } else {
                af = (FloatType) a;
            }

            if (b instanceof IntType) {
                bf = ((IntType) b).toFloatType();
            } else {
                bf = (FloatType) b;
            }

            return af.lt(bf);
        } else {
            return ((IntType) a).lt((IntType) b);
        }
    }


    public static boolean lte(NumType a, NumType b) {
        if (a instanceof FloatType || b instanceof FloatType) {
            FloatType af;
            FloatType bf;

            if (a instanceof IntType) {
                af = ((IntType) a).toFloatType();
            } else {
                af = (FloatType) a;
            }

            if (b instanceof IntType) {
                bf = ((IntType) b).toFloatType();
            } else {
                bf = (FloatType) b;
            }

            return af.lte(bf);
        } else {
            return ((IntType) a).lte((IntType) b);
        }
    }


    public static boolean gt(NumType a, NumType b) {
        if (a instanceof FloatType || b instanceof FloatType) {
            FloatType af;
            FloatType bf;

            if (a instanceof IntType) {
                af = ((IntType) a).toFloatType();
            } else {
                af = (FloatType) a;
            }

            if (b instanceof IntType) {
                bf = ((IntType) b).toFloatType();
            } else {
                bf = (FloatType) b;
            }

            return af.gt(bf);
        } else {
            return ((IntType) a).gt((IntType) b);
        }
    }


    public static boolean gte(NumType a, NumType b) {
        if (a instanceof FloatType || b instanceof FloatType) {
            FloatType af;
            FloatType bf;

            if (a instanceof IntType) {
                af = ((IntType) a).toFloatType();
            } else {
                af = (FloatType) a;
            }

            if (b instanceof IntType) {
                bf = ((IntType) b).toFloatType();
            } else {
                bf = (FloatType) b;
            }

            return af.gte(bf);
        } else {
            return ((IntType) a).gte((IntType) b);
        }
    }


    public static boolean eq(NumType a, NumType b) {
        if (a instanceof FloatType || b instanceof FloatType) {
            FloatType af;
            FloatType bf;

            if (a instanceof IntType) {
                af = ((IntType) a).toFloatType();
            } else {
                af = (FloatType) a;
            }

            if (b instanceof IntType) {
                bf = ((IntType) b).toFloatType();
            } else {
                bf = (FloatType) b;
            }

            return af.eq(bf);
        } else {
            return ((IntType) a).eq((IntType) b);
        }
    }


    public static boolean isZero(NumType a) {
        if (a instanceof FloatType) {
            return ((FloatType) a).isZero();
        } else if (a instanceof IntType) {
            return ((IntType) a).isZero();
        } else {
            return false;
        }
    }


    public static boolean isFeasible(NumType a) {
        if (a instanceof FloatType) {
            return ((FloatType) a).isFeasible();
        } else if (a instanceof IntType) {
            return ((IntType) a).isFeasible();
        } else {
            return false;
        }
    }


    public static void setLowerInclusive(NumType a, NumType b) {
        if (a instanceof IntType && b instanceof IntType) {
            ((IntType) a).setLowerInclusive((IntType) b);
        }

        if (a instanceof FloatType && b instanceof FloatType) {
            ((FloatType) a).setLowerInclusive((FloatType) b);
        }

        if (a instanceof IntType && b instanceof FloatType) {
            ((IntType) a).setLowerInclusive((FloatType) b);
        }

        if (a instanceof FloatType && b instanceof IntType) {
            ((FloatType) a).setLowerInclusive((IntType) b);
        }
    }


    public static void setLowerExclusive(NumType a, NumType b) {
        if (a instanceof IntType && b instanceof IntType) {
            ((IntType) a).setLowerExclusive((IntType) b);
        }

        if (a instanceof FloatType && b instanceof FloatType) {
            ((FloatType) a).setLowerExclusive((FloatType) b);
        }

        if (a instanceof IntType && b instanceof FloatType) {
            ((IntType) a).setLowerExclusive((FloatType) b);
        }

        if (a instanceof FloatType && b instanceof IntType) {
            ((FloatType) a).setLowerExclusive((IntType) b);
        }
    }


    public static void setUpperInclusive(NumType a, NumType b) {
        if (a instanceof IntType && b instanceof IntType) {
            ((IntType) a).setUpperInclusive((IntType) b);
        }

        if (a instanceof FloatType && b instanceof FloatType) {
            ((FloatType) a).setUpperInclusive((FloatType) b);
        }

        if (a instanceof IntType && b instanceof FloatType) {
            ((IntType) a).setUpperInclusive((FloatType) b);
        }

        if (a instanceof FloatType && b instanceof IntType) {
            ((FloatType) a).setUpperInclusive((IntType) b);
        }
    }


    public static void setUpperExclusive(NumType a, NumType b) {
        if (a instanceof IntType && b instanceof IntType) {
            ((IntType) a).setUpperExclusive((IntType) b);
        }

        if (a instanceof FloatType && b instanceof FloatType) {
            ((FloatType) a).setUpperExclusive((FloatType) b);
        }

        if (a instanceof IntType && b instanceof FloatType) {
            ((IntType) a).setUpperExclusive((FloatType) b);
        }

        if (a instanceof FloatType && b instanceof IntType) {
            ((FloatType) a).setUpperExclusive((IntType) b);
        }
    }

}
