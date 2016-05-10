#pragma version(1)
#pragma rs java_package_name(com.example.michael.imageblurrer)

float weight;

static uchar getInvertedColor(uchar color) {
	return color*(1 - weight) + weight*(255 - color);
}

uchar4 __attribute__((kernel)) invert(uchar4 in, uint32_t x, uint32_t y) {
	uchar4 out = in;
	out.r = getInvertedColor(in.r);
	out.g = getInvertedColor(in.g);
	out.b = getInvertedColor(in.b);
	return out;
}