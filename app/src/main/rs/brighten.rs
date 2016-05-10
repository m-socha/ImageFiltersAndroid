#pragma version(1)
#pragma rs java_package_name(com.example.michael.imageblurrer)

float weight;

uchar4 __attribute__((kernel)) brighten(uchar4 in, uint32_t x, uint32_t y) {
	uchar4 out = in;
	float multFactor = 4*weight + 1;
	out.r = (1 - weight)*in.r + weight*in.r*multFactor;
	out.g = (1 - weight)*in.g + weight*in.g*multFactor;
	out.b = (1 - weight)*in.b + weight*in.b*multFactor;
	return out;
}