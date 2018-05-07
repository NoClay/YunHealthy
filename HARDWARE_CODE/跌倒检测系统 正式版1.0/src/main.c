/***************************************************************
�������ϵͳ1.2���԰�

����˵����
δ����ʱ����������������ʾһ�£�û���κ�ʵ����;
δ����ʱ�����������н����������˱�������ȡ��
�����󱨾�ʱ����������ȡ������
�����������ڲ����������Զ����ͱ�������
��λ���ŷ�����Ϻ�ᱨ��400ms


�����Ҫ���ã����кܶ�ϸ����Ҫ����
���磬mpu6050����ͣ��
gsm����ػ�
��ص�ѹ����
stm32��������
���Ź�
���ֲ���ʧ��
����

˵����
gsmģ���ֶ��趨Ϊ�ر��Զ����ò����ʣ��ֶ����ò�����Ϊ9600

����һ��Ҫ�����ⲿ�ж��ϣ���������
����һ��Ҫ�����ⲿ�ж��ϣ���������
����һ��Ҫ�����ⲿ�ж��ϣ���������

��ʾ��˵����
����2����ϵͳ�����ɹ�
����5����gsmģ��������ʱ��ʧ��
����3��������ģ�������ɹ�

�ӿ�˵����
PA2 USART2 TX
PA3 USART2 RX

PA4 GPSʹ�� �͵�ƽ����
PA5 GSMʹ�� �͵�ƽ����
PA7 GSM��λ �͵�ƽ��λ

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
led ���
������ ���
���� ���
usart1 ���
usart2 ���STM32��������������δ����Ҳ���ز���
usart3 ���STM32��������������δ����
mpu6050 ���
gsm �ɹ��������������󾭳�����ʧ��
gps ���
gsmʹ�� ���
gpsʹ�� ���
gsm���߼����� ����ʧ�ܡ�����ʧ��
gps���߼����� ���
�����ж� ���


Ŀǰ���ڵ����⣺
gsmģ�������������Ͷ��š��Ӵ�绰ʱ���mpu6050�����޴���ţ���������
gsmģ�������������Ͷ��š��Ӵ�绰ʱ���mpu6050�����޴���ţ���������
gsmģ�������������Ͷ��š��Ӵ�绰ʱ���mpu6050�����޴���ţ���������

gsmģ�������ɹ����Ի��6050������΢���ţ��������Ϊ6050������ë��

ż���ᷢ��gsm�����ɹ�������ʾʧ��

gps�źŲ���ʱ�����ܻ����gpsָʾ����˸��ʾ��λ�ɹ����������Ͷ�λ���ŵ����

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
void NoGsmInitialize(void);
void ThisSendFalldownMessage(void);
void ThisSendAlarmMessage(void);


int main(void)
{
	unsigned char fall = 0;//������ʶ

	initialize();
//	NoGsmInitialize();
	
	delay_ms(1000);
	
	while(1)
	{
		ReadAll();//��ȡmpu6050���м��ٶȺͽ��ٶ�����
		GetData();//�ϳ�mpu6050���ٶȺͽ��ٶ�����
		delay_ms(2);
		//SendArray(mpudata, 16);
		fall = FallDown();
		
		if(KeyStatus == 1)
		{
			KeyStatus = 0;
			
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
		}
		
		if(KeyStatus == 2)
		{
			KeyStatus = 0;//����״̬��0

			//����������
			BUZZER = 0;
			delay_ms(800);
			BUZZER = 1;
			
			ThisSendAlarmMessage();
		}
		
		if(fall)
		{
			fall = 0;
			
			ThisSendFalldownMessage();
		}
	}
}

//����ʱ��������λ����������
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

//��ʼ��
void initialize(void)
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
	
	//GSM������ʱ�޷�ʵ��
	//�����ɹ�����gsmģ������
	//GSMSleep();
}

//��ʼ�������ȴ�gsm���������������������������
void NoGsmInitialize(void)
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


//	//������ȴ���ֱ��gsmģ���ʼ�����
//	i = 0;
//	while(!GSMReady())
//	{
//		delay_ms(1);
//		i++;
//		
//		//�������û������ʾgsmģ��������ʱ��ʧ��
//		//����ʧ�ܣ���gsmģ�鸴λ������ֱ�������ɹ�
//		if(i > 30000)
//		{
//			BuzzerWork(100,50,5);//����5����gsmģ��������ʱ��ʧ��
//			GSM_RST = 0;
//			delay_ms(10);
//			GSM_RST = 1;
//			i = 0;
//		}
//	}
//	
//	BuzzerWork(200,80,3);//����3��������ģ�������ɹ�
//	
//	//GSM������ʱ�޷�ʵ��
//	//�����ɹ�����gsmģ������
//	//GSMSleep();
}

//�������������ͱ������š���λ�����Ͷ�λ���ŵ�ȫ������
void ThisSendFalldownMessage(void)
{
	unsigned char i;
	
	//ͨ���޸�ѭ�������ͷ���������ʱ�������Ʊ���ʱ��
	for(i=0; i<30; i++)
	{
		if(KeyStatus == 1)
		{
			KeyStatus = 0;
			return;
		}
		
		BUZZER = 0;
		delay_ms(500);
		BUZZER = 1;
		delay_ms(300);
	}
	
	//GSM���Ѽ�������ʱ�޷�ʵ��
	//GSMWakeup();
	
	SendFallDownMessage();
	
	//gpsʹ�ܺ󣬵ȴ�300����������$GPTXT��Ϣ���֮꣬���յ�������ȫ����$GPRMC��λ��Ϣ
	GPS_EN = 1;
	delay_ms(300);
	USART2_RX_STA = 0;
	
	while(1)
	{
		if(USART2_RX_STA & 0x8000)
		{
			//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
			if(getgps((char *)USART2_RX_BUF))
			{
				GPS_EN = 0;
				SendLocationMessage();
				break;
			}
			USART2_RX_STA = 0;
		}
	}
	
	BUZZER = 0;
	delay_ms(400);
	BUZZER = 1;
}

//�������������ͱ������š���λ�����Ͷ�λ���ŵ�ȫ������
void ThisSendAlarmMessage(void)
{	
	SendAlarmMessage();
	
	//gpsʹ�ܺ󣬵ȴ�300����������$GPTXT��Ϣ���֮꣬���յ�������ȫ����$GPRMC��λ��Ϣ
	GPS_EN = 1;
	delay_ms(300);
	USART2_RX_STA = 0;
				
	while(1)
	{
		if(USART2_RX_STA & 0x8000)
		{
			//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
			
			if(getgps((char *)USART2_RX_BUF))
			{
				GPS_EN = 0;
				SendLocationMessage();
				break;
			}
			USART2_RX_STA = 0;
		}
	}
	
	BUZZER = 0;
	delay_ms(400);
	BUZZER = 1;
}
