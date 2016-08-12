#pragma version(1)
#pragma rs java_package_name(com.example.michael.imageblurrer)

float weight;

uchar4 __attribute__((kernel)) brighten(uchar4 in, uint32_t x, uint32_t y) {
	uchar4 out = in;
	out.r = (1 - weight)*in.r + weight*255;
	out.g = (1 - weight)*in.g + weight*255;
	out.b = (1 - weight)*in.b + weight*255;
	return out;
}