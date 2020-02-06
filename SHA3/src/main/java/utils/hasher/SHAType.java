package utils.hasher;

public enum SHAType {
    SHA224, SHA256, SHA384, SHA512;

    public Parameters get() {

        switch (SHAType.valueOf(this.toString())) {
            case SHA224:
                return new Parameters(1152, 448, 224);
            case SHA256:
                return new Parameters(1088, 512, 256);
            case SHA384:
                return new Parameters(832, 768, 384);
            default:
                return new Parameters(576, 1024, 512);
        }
    }

     public static class Parameters {
        private int r, c, out;

        Parameters(final int r,
                   final int c,
                   final int out) {
            this.r = r;
            this.c = c;
            this.out = out;
        }

        public int getR() {
            return r;
        }

        public int getC() {
            return c;
        }

        public int getOut() {
            return out;
        }
    }
}
