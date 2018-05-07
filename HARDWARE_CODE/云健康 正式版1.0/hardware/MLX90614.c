#include "STMIIC.h"
#include "delay.h"

//MLX90614的IIC通讯速率比较低，需要将IIC通信里的延时适当加长，否则无法正常工作
float ReadTemperatureMLX90614(void)
{
	unsigned char a,b,c;
	float temp = 0;
	
	//初始化，MLX90614上电后默认在PWM模式下工作，将SCL拉低1.44ms以上会转换为IIC工作模式
	SCL = 1;
	delay_ms(10);
	SCL = 0;
	delay_ms(10);
	SCL = 1;
	delay_ms(10);
	
	IICstart();
	IICwrite(0x00);//所有MLX90614都会响应0x00的地址，多个MLX90614可以使用0x5A的地址
	SlaRes();
	IICwrite(0x07);//温度寄存器的地址
	SlaRes();
	
	IICstart();
	IICwrite(0x01);
	SlaRes();
	a = IICread();//温度数据的低八位
	MasRes();
	b = IICread();//温度数据的高八位
	MasRes();
	c = IICread();//PEC校验。我不会PEC校验，所以这两句可以不要。
	NoRes();
	IICstop();
	
	temp = ((b << 8) + a) * 0.02 - 273.15;//根据手册上提供的公式转换温度
	
	return temp;
}