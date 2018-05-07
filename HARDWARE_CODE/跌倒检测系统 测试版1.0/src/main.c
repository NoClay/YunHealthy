/***************************************************************
�������ϵͳ1.0���԰�

˵����
gsmģ���ֶ��趨Ϊ�ر��Զ����ò����ʣ��ֶ����ò�����Ϊ9600

�����ƹ��ܣ�
����������
gsm���߼�����
gps���߼�����
��������ȫ����д

���Ź���һ��Ҫ��������һ��
���Ź���һ��Ҫ��������һ��
���Ź���һ��Ҫ��������һ��

�ӿ�˵����
PA2 USART2 TX
PA3 USART2 RX

PA4 GPSʹ�� �͵�ƽ����
PA5 GSMʹ�� �͵�ƽ����

PA9 USART1 TX
PA10 USART1 RX

PA13 SWCLK
PA14 SWDIO

PB0 mpu6050��SDA
PB1 mpu6050��SCL

PB6 ����24C02��SCL
PB7 ����24C02��SDA

PB10 USART3 TX
PB11 USART3 RX

PB13 ����
PB15 ������

PD2 ָʾ��

����Ӳ����
led y
������ y
���� y
usart1 ���STM32��������������δ����
usart2 ���STM32��������������δ����Ҳ���ز���
usart3 ���STM32��������������δ����
mpu6050 y
gsm �ɹ�������������ʧ��
gps �ɹ�����δ��λ
gsmʹ�� �����ߣ�����δ����
gpsʹ�� y

Ŀǰ���ڵ�����
6050������ �ѽ��
gsmģ�������������Ͷ��š��Ӵ�绰ʱ���mpu6050�����޴���ţ���������
gsmģ�������������Ͷ��š��Ӵ�绰ʱ���mpu6050�����޴���ţ���������
gsmģ�������������Ͷ��š��Ӵ�绰ʱ���mpu6050�����޴���ţ���������

Ӧ���ȴ��յ�SMS Ready\r\n������mpu6050

gsmģ�������ɹ����Ի��6050������΢���ţ��������Ϊ6050������ë��

gps�����иĶ������ܻ�����⣡����������������������

������ʱ������Ҫ�޸ģ���������������while��һȦ��ʱ��䳤��

***************************************************************/

#include "sys.h"
#include "delay.h"
#include "key.h"
#include "led.h"
#include "usart.h"
#include "usart2.h"
#include "usart3.h"
#include "gsm.h"
#include "gps.h"
#include "mpu6050.h"
#include "falldown.h"
#include "buzzer.h"
#include "io.h"
#include "STMIIC.h"
#include <math.h>


void SendArray(unsigned char *ArrayName, unsigned short int length);
void initialize(void);


int main(void)
{
//	short int a,b,c;
	unsigned char i, s = 0;
//	
//	Stm32_Clock_Init(9);//��Ƶ����Ϊ2������Ƶ16MHz
//	delay_init(72);
//	
//	//ֻ�д���1��ȫ�٣��������ڵ��ٶȶ��Ǵ���1��һ��
//	
//	uart_init(72, 9600);//����GSMģ��
//	USART2_Init(36, 9600);//����GPSģ��
//	USART3_Init(36, 9600);//���ڲ���
//	
//	
//	Stm32_Clock_Init(2);//��Ƶ����Ϊ2������Ƶ16MHz
//	delay_init(16);
//	
//	//ֻ�д���1��ȫ�٣��������ڵ��ٶȶ��Ǵ���1��һ��
//	uart_init(16, 9600);//����GSMģ��
//	USART2_Init(8, 9600);//����GPSģ��
//	USART3_Init(8, 9600);//���ڲ���
//	
//	IICinit();
//	
//	initmpu6050();
//	
//	//ת���绰����ĸ�ʽ
//	ChangeNumber(GSM_AN, AddresseeNumber);
//	ChangeNumber(GSM_SSCN, ServiceCenterNumber);
//	
//	//��������ǰ��ȡUSART1->SR������Ӳ����λ�󴮿ڷ������ݵ�һ�ֽڶ�ʧ���������
//	i = USART1->SR;
//	
//	LED_Init();
//	LED = 1;//Ϩ��led
//	
//	Buzzer_Init();
//	BUZZER = 1;//�رշ�����
//	
//	Key_Init();
//	IO_Init();
//	
//	GPS_EN = 0;//�ر�GPS
//
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	
//	SendAlarmMessage();
//	SendFallDownMessage();
//	
//	GPS_EN = 0;
//	
//	SendGSMCommand("at+csclk=1\r\n");//����
//	
//	for(i=0; i<5; i++)
//			{
//				LED = 0;
//				delay_ms(100);
//				LED = 1;
//				delay_ms(100);
//			}
//	
//	BUZZER = 0;
//	delay_ms(100);
//	BUZZER = 1;
//	delay_ms(100);
//	BUZZER = 0;
//	delay_ms(100);
//	BUZZER = 1;

	initialize();
	
	BUZZER = 0;
	delay_ms(200);
	BUZZER = 1;
	delay_ms(50);
	BUZZER = 0;
	delay_ms(200);
	BUZZER = 1;
	delay_ms(50);
	BUZZER = 0;
	delay_ms(200);
	BUZZER = 1;
	
	while(1)
	{
		ReadAll();//��ȡmpu6050���м��ٶȺͽ��ٶ�����
		GetData();//�ϳ�mpu6050���ٶȺͽ��ٶ�����
		SendArray(mpudata, 16);
		
		if(FallDown())
		{
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
			delay_ms(100);
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
		}
	}

/*
	while(1)
	{
		i = Key_Scan();//����������������
		
		if(i == 1)
		{
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
			i = 0;
		}
		
		if(i == 2)
		{
			BUZZER = 0;
			delay_ms(300);
			BUZZER = 1;
			i = 0;
		}
		
		ReadAll();//��ȡmpu6050���м��ٶȺͽ��ٶ�����
		GetData();//�ϳ�mpu6050���ٶȺͽ��ٶ�����
		//SendArray(mpudata, 16);
		//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
		//SendArray(USART_RX_BUF, USART_RX_STA & 0x7FFF);
		
		if(FallDown())
		{
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
			delay_ms(100);
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
		}
		delay_ms(2);
		

		if(GSMReady())
		{
			s = 1;
		}
		
		if(s==1)
		{
			SendGSMCommand("at+csclk=1\r\n");
			delay_ms(100);
			SendFallDownMessage();
			s=2;
			
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
		}
		if(s==2)
		{
			GSM_EN = 0;
			SendGSMCommand("at+csclk=0\r\n");
			s=3;
			
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
			delay_ms(100);
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
		}
		if(s==3)
		{
			SendAlarmMessage();
			
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
			delay_ms(100);
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
			delay_ms(100);
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
		}
		
//		if(USART_RX_STA & 0x8000)
//		{
//			USART_RX_STA = 0;
//			for(i=0; i<10; i++)
//			{
//				USART3->DR = USART_RX_BUF[i];
//				while ((USART3->SR & 0x40)==0)
//				{
//				}
//			}
//		}
		
		
//			for(i=0; i<5; i++)
//			{
//				USART3->DR = 'G';
//				while ((USART3->SR & 0x40)==0)
//				{
//				}
//			}
			
//				LED = 0;
//				delay_ms(200);
//				LED = 1;
//				delay_ms(200);
//				LED = 0;
//				delay_ms(200);
//				LED = 1;
//				delay_ms(200);
	}
*/

/*	
	while(1)
	{
//		LED = 1;
//		delay_ms(400);
//		LED = 0;
//		delay_ms(400);
		
		ReadAll();//��ȡmpu6050���м��ٶȺͽ��ٶ�����
		GetData();//�ϳ�mpu6050���ٶȺͽ��ٶ�����
		SendArray(mpudata, 16);
		//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
		//SendArray(USART_RX_BUF, USART_RX_STA & 0x7FFF);
		
		if(FallDown())
		{
			for(i=0; i<5; i++)
			{
				LED = 0;
				delay_ms(100);
				LED = 1;
				delay_ms(100);
			}
		}
		delay_ms(2);
	}
*/
/*
	while(1)
	{

//		LED = 1;
//		delay_ms(400);
//		LED = 0;
//		delay_ms(400);
	
		
		ReadAll();//��ȡmpu6050���м��ٶȺͽ��ٶ�����
		GetData();//�ϳ�mpu6050���ٶȺͽ��ٶ�����
		//SendMpu(mpudata, 12);
		
		if(FallDown())
		{
			LED = 0;//����led
			
		}
	
		
		if(USART2_RX_STA & 0x8000)
		{
			//printf("\n");
			//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
			
			//printf("## 001 ##\n");
			if(getgps(USART2_RX_BUF))
			{
				printf("get gps\n");
				printf("N:%f E:%f \n",ChangeCoord(latitude), ChangeCoord(longtitude));
				SendLocationMessage();
				
			}
			else
			{
				printf("no gps\n");
			}
			
			USART2_RX_STA &= 0x0000;
		}
		else
		{
			printf("not get com2\n");
		}
		
	}
	*/
}

void SendArray(unsigned char *ArrayName, unsigned short int length)
{
	unsigned short int i;
	unsigned char sum = 0;

	//��������
	for (i=0; i<length; i++)
	{
		sum += ArrayName[i];
		USART3->DR = ArrayName[i];
		while ((USART3->SR & 0x40)==0)
		{
		}
	}
	
	USART3->DR = sum;
	while ((USART3->SR & 0x40)==0)
	{
	}
}

void initialize()
{
	short int i;
	
	Stm32_Clock_Init(2);//��Ƶ����Ϊ2������Ƶ16MHz
	delay_init(16);//��ʱ��ʼ��
	
	//ֻ�д���1��ȫ�٣��������ڵ��ٶȶ��Ǵ���1��һ��
	uart_init(16, 9600);//����GSMģ��
	USART2_Init(8, 9600);//����GPSģ��
	USART3_Init(8, 9600);//���ڲ���
	
	//��������ǰ��ȡUSART1->SR������Ӳ����λ�󴮿ڷ������ݵ�һ�ֽڶ�ʧ���������
	i = USART1->SR;
	i = USART2->SR;
	i = USART3->SR;
	
	//ת���绰����ĸ�ʽ
	ChangeNumber(GSM_AN, AddresseeNumber);
	ChangeNumber(GSM_SSCN, ServiceCenterNumber);
	
	LED_Init();//led�ӿڳ�ʼ��
	LED = 1;//Ϩ��led
	
	Buzzer_Init();//�������ӿڳ�ʼ��
	BUZZER = 1;//�رշ�����

	Key_Init();//������ʼ��
	IO_Init();//GPS��GSMʹ�ܶ˳�ʼ��
	
	GPS_EN = 0;//�ر�GPS
	
	delay_ms(500);//mpu6050��ʼ��ǰ��һ����ʱ
	
	IICinit();//iic��ʼ��
	initmpu6050();//mpu6050��ʼ�������Ǳ����gsm��ȫ�������ٹ���
	
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);

	BUZZER = 0;
	delay_ms(80);
	BUZZER = 1;
	delay_ms(40);
	BUZZER = 0;
	delay_ms(80);
	BUZZER = 1;

//gsmģ����ܻ��ʼ��ʧ�ܣ�Ӧ����������һ��
	while(!GSMReady());//������ȴ���ֱ��gsmģ���ʼ�����
}
