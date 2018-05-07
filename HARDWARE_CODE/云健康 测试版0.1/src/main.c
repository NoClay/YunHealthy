/***************************************************************
�ƽ���ϵͳ1.0���԰�

��������Ǵӵ�����⸴�ƹ����ģ�ע��iic�Ľӿ�

�費��Ҫ������

�ӿ�˵����
PA2 USART2 TX
PA3 USART2 RX

PA4
PA5
PA7

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

PB13 
PB15 

PD2 ָʾ��

����Ӳ����
led
Ѫ��
Ѫ��
�ĵ�
�¶�
�۳�


Ŀǰ���ڵ����⣺


***************************************************************/

#include "sys.h"
#include "delay.h"
#include "key.h"
#include "led.h"
#include "usart.h"
#include "usart2.h"
#include "usart3.h"

#include "adc.h"
#include "STMIIC.h"
#include "SendPack.h"
//#include ""
//#include ""

#include "gsm.h"
#include "gps.h"
#include "mpu6050.h"
#include "falldown.h"
#include "buzzer.h"
#include "io.h"
//#include "STMIIC.h"
#include <math.h>

//�ƽ���������ļ�
/*
adc.h
iic
sendpack
*/

void SendArray(unsigned char *ArrayName, unsigned short int length);
unsigned char initialize(void);



int main(void)
{
//	short int a,b,c;
	unsigned char i, f = 0;

	initialize();
	
	delay_ms(1000);
	
//	GSMSleep();
	
//	if(GSMSleep())
//	{
//		USART3->DR = 'S';
//		while ((USART3->SR & 0x40)==0)
//		{
//		}
//	}

//	//gpsʹ�ܺ󣬵ȴ�300����������$GPTXT��Ϣ���֮꣬���յ�������ȫ����$GPRMC��λ��Ϣ
//	GPS_EN = 1;
//	delay_ms(300);
//	USART2_RX_STA = 0;

/*	
	while(1)
	{
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
			
			USART2_RX_STA = 0;
		}
	}
*/


	
	while(1)
	{
		//i = Key_Scan();//����������������
		
		ReadAll();//��ȡmpu6050���м��ٶȺͽ��ٶ�����
		GetData();//�ϳ�mpu6050���ٶȺͽ��ٶ�����
		delay_ms(2);
		//SendArray(mpudata, 16);
		f = FallDown();
		
		if(KeyStatus == 1)
		{
			KeyStatus = 0;
			
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
			
			i = 0;
			
//			if(GSMWakeup())
//			{
//				USART3->DR = 'W';
//				while ((USART3->SR & 0x40)==0)
//				{
//				}
//			}
			
			//gpsʹ�ܺ󣬵ȴ�300����������$GPTXT��Ϣ���֮꣬���յ�������ȫ����$GPRMC��λ��Ϣ
			GPS_EN = 1;
			delay_ms(300);
			USART2_RX_STA = 0;
			
			//SendFallDownMessage();
			
			while(1)
			{
				if(USART2_RX_STA & 0x8000)
				{
					//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
					
					if(getgps(USART2_RX_BUF))
					{
						//printf("get gps\n");
						//printf("N:%f E:%f \n",ChangeCoord(latitude), ChangeCoord(longtitude));
						GPS_EN = 0;
						
						SendLocationMessage();
						break;
					}
					
					USART2_RX_STA = 0;
				}
				
//				if(USART2_RX_STA & 0x8000)
//				{
//					for (i=0; i<40; i++)
//					{
//						USART3->DR = USART2_RX_BUF[i];
//						while ((USART3->SR & 0x40)==0)
//						{
//						}
//					}
//				}
//				
//				if(getgps(USART2_RX_BUF))
//				{
//					//printf("get gps\n");
//					//printf("N:%f E:%f \n",ChangeCoord(latitude), ChangeCoord(longtitude));
//					GPS_EN = 0;
//					SendLocationMessage();
//					break;
//				}
			}
			
			BUZZER = 0;
			delay_ms(400);
			BUZZER = 1;
						
			//SendLocationMessage();
		}
		
		if(KeyStatus == 2)
		{
			KeyStatus = 0;
			
			BUZZER = 0;
			delay_ms(300);
			BUZZER = 1;
			i = 0;
			
		//	SendFallDownMessage();
		}
		
		if(f)
		{
			for(i=0; i<20; i++)
			{
				if(KeyStatus == 1)
				{
					KeyStatus = 0;
					break;
				}
				
				BUZZER = 0;
				delay_ms(400);
				BUZZER = 1;
				delay_ms(200);
			}
			//GSMWakeup();
			//SendFallDownMessage();
			f = 0;
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

//�����ɹ�����0
//gsmģ������ʧ�ܷ���1
unsigned char initialize()
{
	unsigned short int i;
	
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
	GSM_EN = 1;//ʹ��GSM
	GSM_RST = 1;//����λGSM
	
	delay_ms(500);//mpu6050��ʼ��ǰ��һ����ʱ
	
	IICinit();//iic��ʼ��
	initmpu6050();//mpu6050��ʼ�������Ǳ����gsm��ȫ�������ٹ���

	BuzzerWork(80,40,2);//����2����ϵͳ�����ɹ�


	//������ȴ���ֱ��gsmģ���ʼ�����
	i = 0;
	while(!GSMReady())
	{
		delay_ms(1);
		i++;
		
		//�������û������ʾgsmģ��������ʱ��ʧ��
		//����ʧ�ܣ���gsmģ�鸴λ������ֱ�������ɹ�
		if(i > 30000)
		{
			BuzzerWork(100,50,5);//����5����gsmģ��������ʱ��ʧ��
			GSM_RST = 0;
			delay_ms(10);
			GSM_RST = 1;
			i = 0;
		}
	}
	
	BuzzerWork(200,80,3);//����3��������ģ�������ɹ�
	
	//�����ɹ�����gsmģ������
	//SendGSMCommand("at+csclk=1\r\n");
	//if(���߳ɹ�)��������
	
	return 0;//gsmģ�������ɹ�
}
