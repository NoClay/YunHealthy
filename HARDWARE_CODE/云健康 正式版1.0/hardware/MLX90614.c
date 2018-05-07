#include "STMIIC.h"
#include "delay.h"

//MLX90614��IICͨѶ���ʱȽϵͣ���Ҫ��IICͨ�������ʱ�ʵ��ӳ��������޷���������
float ReadTemperatureMLX90614(void)
{
	unsigned char a,b,c;
	float temp = 0;
	
	//��ʼ����MLX90614�ϵ��Ĭ����PWMģʽ�¹�������SCL����1.44ms���ϻ�ת��ΪIIC����ģʽ
	SCL = 1;
	delay_ms(10);
	SCL = 0;
	delay_ms(10);
	SCL = 1;
	delay_ms(10);
	
	IICstart();
	IICwrite(0x00);//����MLX90614������Ӧ0x00�ĵ�ַ�����MLX90614����ʹ��0x5A�ĵ�ַ
	SlaRes();
	IICwrite(0x07);//�¶ȼĴ����ĵ�ַ
	SlaRes();
	
	IICstart();
	IICwrite(0x01);
	SlaRes();
	a = IICread();//�¶����ݵĵͰ�λ
	MasRes();
	b = IICread();//�¶����ݵĸ߰�λ
	MasRes();
	c = IICread();//PECУ�顣�Ҳ���PECУ�飬������������Բ�Ҫ��
	NoRes();
	IICstop();
	
	temp = ((b << 8) + a) * 0.02 - 273.15;//�����ֲ����ṩ�Ĺ�ʽת���¶�
	
	return temp;
}