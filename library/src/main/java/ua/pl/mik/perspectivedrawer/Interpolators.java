/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.pl.mik.perspectivedrawer;

import android.view.animation.Interpolator;

public final class Interpolators {
    private static final float DOMAIN = 1.0f;
    private static final float DURATION = 1.0f;
    private static final float START = 0.0f;

    private Interpolators() {
    }

    public static final class Linear {
        private Linear() {
        }

        public static final Interpolator EASE_NONE = new Interpolator() {
            public float getInterpolation(float input) {
                return input;
            }
        };
    }

    public static final class Cubic {
        private Cubic() {
        }

        public static final Interpolator EASE_IN = new Interpolator() {
            public float getInterpolation(float input) {
                return DOMAIN * (input /= DURATION) * input * input + START;
            }
        };
        public static final Interpolator EASE_OUT = new Interpolator() {
            public float getInterpolation(float input) {
                return DOMAIN * ((input = input / DURATION - 1) * input * input + 1) + START;
            }
        };
        public static final Interpolator EASE_IN_OUT = new Interpolator() {
            public float getInterpolation(float input) {
                return ((input /= DURATION / 2) < 1.0f)
                        ? (DOMAIN / 2 * input * input * input + START)
                        : (DOMAIN / 2 * ((input -= 2) * input * input + 2) + START);
            }
        };
    }

    public static final class Quad {
        private Quad() {
        }

        public static final Interpolator EASE_IN = new Interpolator() {
            public float getInterpolation(float input) {
                return DOMAIN * (input /= DURATION) * input + START;
            }
        };
        public static final Interpolator EASE_OUT = new Interpolator() {
            public float getInterpolation(float input) {
                return -DOMAIN * (input /= DURATION) * (input - 2) + START;
            }
        };
        public static final Interpolator EASE_IN_OUT = new Interpolator() {
            public float getInterpolation(float input) {
                return ((input /= DURATION / 2) < 1)
                        ? (DOMAIN / 2 * input * input + START)
                        : (-DOMAIN / 2 * ((--input) * (input - 2) - 1) + START);
            }
        };
    }

    public static final class Quart {
        private Quart() {
        }

        public static final Interpolator EASE_IN = new Interpolator() {
            public float getInterpolation(float input) {
                return DOMAIN * (input /= DURATION) * input * input * input + START;
            }
        };
        public static final Interpolator EASE_OUT = new Interpolator() {
            public float getInterpolation(float input) {
                return -DOMAIN * ((input = input / DURATION - 1) * input * input * input - 1) + START;
            }
        };
        public static final Interpolator EASE_IN_OUT = new Interpolator() {
            public float getInterpolation(float input) {
                return ((input /= DURATION / 2) < 1)
                        ? (DOMAIN / 2 * input * input * input * input + START)
                        : (-DOMAIN / 2 * ((input -= 2) * input * input * input - 2) + START);
            }
        };
    }

    public static final class Quint {
        private Quint() {
        }

        public static final Interpolator EASE_IN = new Interpolator() {
            public float getInterpolation(float input) {
                return DOMAIN * (input /= DURATION) * input * input * input * input + START;
            }
        };
        public static final Interpolator EASE_OUT = new Interpolator() {
            public float getInterpolation(float input) {
                return DOMAIN * ((input = input / DURATION - 1) * input * input * input * input + 1) + START;
            }
        };
        public static final Interpolator EASE_IN_OUT = new Interpolator() {
            public float getInterpolation(float input) {
                return ((input /= DURATION / 2) < 1)
                        ? (DOMAIN / 2 * input * input * input * input * input + START)
                        : (DOMAIN / 2 * ((input -= 2) * input * input * input * input + 2) + START);
            }
        };
    }

    public static final class Sine {
        private Sine() {
        }

        public static final Interpolator EASE_IN = new Interpolator() {
            public float getInterpolation(float input) {
                return -DOMAIN * (float) Math.cos(input / DURATION * (Math.PI / 2)) + DOMAIN + START;
            }
        };
        public static final Interpolator EASE_OUT = new Interpolator() {
            public float getInterpolation(float input) {
                return DOMAIN * (float) Math.sin(input / DURATION * (Math.PI / 2)) + START;
            }
        };
        public static final Interpolator EASE_IN_OUT = new Interpolator() {
            public float getInterpolation(float input) {
                return -DOMAIN / 2 * ((float) Math.cos(Math.PI * input / DURATION) - 1.0f) + START;
            }
        };
    }

}