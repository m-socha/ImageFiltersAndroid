#pragma version(1)
#pragma rs java_package_name(com.example.michael.imageblurrer)

float weight;

uchar4 __attribute__((kernel)) greyscale(uchar4 in, uint32_t x, uint32_t y) {
	uchar4 out = in;
	uchar greyscaleVal = (0.21*in.r + 0.72*in.g + 0.07*in.b);
	out.r = (1 - weight)*in.r + weight*greyscaleVal;
	out.g = (1 - weight)*in.g + weight*greyscaleVal;
	out.b = (1 - weight)*in.b + weight*greyscaleVal;
	return out;
}