/***************************************************************
STM32ģ��I^2C����ͨѶ

ʹ��ǰ���ڡ�ͷ�ļ��ڡ�����SCL��SDA���߽ӿ�

�� �� ����Inter IC Bus��I^2C���ߣ�
�� д �ˣ���־
��дʱ�䣺2015��7��31��
Ӳ��֧�֣�STM32F103ϵ��
�ӿ�˵�����μ������ע��
�޸���־��2015.04.10�����Ż�write����
          2015.04.16�����������������޸ĸ������������������������������������ĳ�ͻ������ΪI2C_v1.2.c
          2015.07.10�������������
          2015.07.31����51����Ļ�������ֲ��STM32
��    ����V1.0
��    ע������PCF8591P��PCF8563T��MPU6050оƬ��ͨ�����ԣ����й�������������оƬ��δ���ԡ�
***************************************************************/

#include "STMIIC.h"
#include "sys.h"

/*
//����I2C����
sbit SCL = P1^0;//ʱ����
sbit SDA = P1^1;//������

void IICdelay();//������ʱ
void IICinit();//���߳�ʼ����initialise
void IICstart();//������ʼ�ź�
void IICstop();//����ֹͣ�ź�
unsigned char SlaRes();//���豸Ӧ��slave respons
void MasRes();//���豸Ӧ��master respons
void NoRes();//���豸��Ӧ��no respons
void IICwrite(unsigned char iicdata);//дһ���ֽ�
unsigned char IICread();//��һ���ֽ�
void IICWriteOneByte(unsigned char RegisterAddress, unsigned char dat);//��I2C�豸��дһ���ֽ����ݣ�WriteOneByte
unsigned char IICReadOneByte(unsigned char RegisterAddress);//��I2C�豸��ȡһ���ֽ�����
*/

//I2Cͨ��ʹ��delay()����
//һ��_nop_()��ʱ1us������Ҫ����ʱ����4.7us
//��Ƭ��Ƶ��ִ�����ڲ�ͬʱ��ʵ����Ҫ�޸�
void IICdelay()
{
	unsigned char i = 20;
	while(i--);
}

//���߳�ʼ��
//�����߶��������ͷ�����
void IICinit()
{
/******************************/
//�ⲿ���ǳ�ʼ��IO��
/******************************/
/*	
	RCC->APB2ENR |= 0x00000010;//ʹ��PORTCʱ��
	
	//ʱ���ߣ�������ó������ʱ���߶�������������
	GPIOC->CRH &= 0xFFFF0FFF;
	GPIOC->CRH |= 0x00003000;
	GPIOC->ODR |= 0x00000800;
	
	//�����ߣ������ó����
	GPIOC->CRH &= 0xFFF0FFFF;
	GPIOC->CRH |= 0x00030000;
	GPIOC->ODR |= 0x00001000;
*/
	
	//���һ��iic�Ľӿڼ�ֱ����������
	RCC->APB2ENR |= 0x0000008;//ʹ��PORTBʱ��
	
	//ʱ���ߣ�������ó������ʱ���߶�������������
	GPIOB->CRL &= 0xFFFFFFF0;
	GPIOB->CRL |= 0x00000003;
	GPIOB->ODR |= 0x00000001;
	
	//�����ߣ������ó����
	GPIOB->CRL &= 0xFFFFFF0F;
	GPIOB->CRL |= 0x00000030;
	GPIOB->ODR |= 0x00000002;

/******************************/
//�����￪ʼ�������ĳ�ʼ������
/******************************/
//��ʵû��Ҫ��ǰ��Ĵ������Ѿ�������������
	SCL = 1;
	IICdelay();
	SDAO = 1;
	IICdelay();
}

//�����ź�
//SCL�ڸߵ�ƽ�ڼ䣬SDA�ɸߵ�ƽ��͵�ƽ�ı仯����Ϊ�����ź�
void IICstart()
{
	SDA_OUT();//תΪ���ģʽ
	SDAO = 1;
	IICdelay();
	SCL = 1;
	IICdelay();
	SDAO = 0;
	IICdelay();
}

//ֹͣ�ź�
//SCL�ڸߵ�ƽ�ڼ䣬SDA�ɵ͵�ƽ��ߵ�ƽ�ı仯����Ϊֹͣ�ź�
void IICstop()
{
	SDA_OUT();//תΪ���ģʽ
	SDAO = 0;
	IICdelay();
	SCL = 1;
	IICdelay();
	SDAO = 1;
	IICdelay();
}

//Ӧ���ź�
//SCL�ڸߵ�ƽ�ڼ䣬SDA�����豸��Ϊ�͵�ƽ��ʾӦ��
//����(SDA == 1)&&(i<255)��ʾ����һ��ʱ����û���յ���������Ӧ����������Ĭ�ϴ������Ѿ��յ����ݶ����ٵȴ�Ӧ���ź�
//��û���ⲿ�ִ��룬�����ͣ�����ʵ�������в������������﷢�������߸�Ϊreturn ture��false���������

//�ȴ����豸����Ӧ��
unsigned char SlaRes()
{
	unsigned char i = 0;
	
	SDA_IN();//תΪ����ģʽ
	SCL = 1;
	IICdelay();
//	while((SDA == 1)&&(i<254))
//		i++;
	while(SDAI)
	{
		i++;
		if(i > 254)
		{
			IICstop();
			return 1;
		}
	}
	SCL = 0;
	IICdelay();

	return 0;
}

//�����豸����Ӧ��
//���豸�ڷ�����һ�ֽں�SDA���ߣ�SDA���ͷţ������豸��SDA���Ͳ���Ӧ��
void MasRes()
{
	SDA_OUT();//תΪ���ģʽ
	
	SDAO = 0;//ֱ�ӽ�SDA���ͣ��ٲ���SCL
	IICdelay();
	SCL = 1;
	IICdelay();
	SCL = 0;
	IICdelay();
}

//���豸��Ӧ��
void NoRes()
{
	SDA_OUT();//תΪ���ģʽ
	
	SDAO = 1;//SDA�ߵ�ƽΪ��Ӧ��
	IICdelay();
	SCL = 1;
	IICdelay();
	SCL = 0;
	IICdelay();
}

//д��
//���з���һ���ֽ�ʱ����Ҫ������ֽ��е�8λһλһλ�ط���ȥ����temp=temp<<1;��
//��ʾ��temp����һλ�����λ������PSW�Ĵ�����CYλ�У�Ȼ��CY����SDA������SCL�Ŀ����·��ͳ�ȥ
//���SDA���ߣ��Եȴ����豸����Ӧ��
void IICwrite(unsigned char iicdata)
{
	unsigned char i;
	
	SDA_OUT();//תΪ���ģʽ
	for(i=0; i<8; i++)
	{
		//iicdata = iicdata << 1;
		SCL = 0;
		IICdelay();
		
		SDAO = (iicdata & 0x80) >> 7;
		iicdata <<= 1;
		IICdelay();
		
		SCL = 1;
		IICdelay();
	}
	SCL = 0;
	IICdelay();
	SDAO = 1;
	IICdelay();
}

//����
//���н���һ���ֽ�ʱ���ˡ���8λһλһλ�ؽ��ܣ�Ȼ������ϳ�һ���ֽڣ�
//���붨����k����k����һλ����SDA���С������㣬һ�ΰ�8��������λ����һ���ֽ�������ɽ���
//˵��һ�㣬Ϊʲô����Ҫk=0����䣬��Ϊ���ƺ��λ�Զ����㣬����11111111���ƺ��Ϊ11111110
unsigned char IICread()
{
	unsigned char i, k;
	
	SDA_IN();//תΪ����ģʽ
	SCL = 0;
	IICdelay();
	//���ɣ��������б�Ҫ��
	//SDA = 1;
	//IICdelay();
	for(i=0; i<8; i++)
	{
		SCL = 1;
		IICdelay();
		k = (k<<1)|SDAI;
		SCL = 0;
		IICdelay();
	}
	IICdelay();
	return k;
}

//��I2C�豸д��һ���ֽ�����
void IICWriteOneByte(unsigned char DeviceAddress, unsigned char RegisterAddress, unsigned char dat)
{
	IICstart();//��ʼ�ź�
	IICwrite(DeviceAddress);//�����豸��ַ+д�ź�
	SlaRes();
	IICwrite(RegisterAddress);//�ڲ��Ĵ�����ַ��
	SlaRes();
	IICwrite(dat);//�ڲ��Ĵ������ݣ�
	SlaRes();
	IICstop();//����ֹͣ�ź�
}

//��I2C�豸��ȡһ���ֽ�����
unsigned char IICReadOneByte(unsigned char DeviceAddress, unsigned char RegisterAddress)
{
	unsigned char dat;
	IICstart();//��ʼ�ź�
	IICwrite(DeviceAddress);//�����豸��ַ+д�ź�
	SlaRes();
	IICwrite(RegisterAddress);//���ʹ洢��Ԫ��ַ����0��ʼ	
	SlaRes();
	IICstart();//��ʼ�ź�
	IICwrite(DeviceAddress+1);//�����豸��ַ+���ź�
	SlaRes();
	dat = IICread();//�����Ĵ�������
	NoRes();//��Ӧ���ź�
	IICstop();//ֹͣ�ź�
	
	return dat;
}
