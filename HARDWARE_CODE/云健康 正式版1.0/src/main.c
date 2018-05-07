/***************************************************************
�ƽ���ϵͳ1.0��ʽ��

Ѫ���Ǻ��¶�ģ��ƽʱ��Ҫͬʱ����
��ȡѪ������ʱһ��һ��һ��һ��һ��һ��һ�����������¶�ģ��
I2C�����г�ͻ��������������

Ѫ�������Ӻ��Ժ󣬰�key0��ȡ����


����5��pd2�ϻ��и�led�����ܻ��ͨ�����Ӱ�죡��������������������������������������������������������

�ӿ�ʹ�ã�
PA1 AD �ĵ�
PA2 USART2 TX ��
PA3 USART2 RX �۳�
PA4 SDA
PA5 SCL
PA9 USART1 TX ����
PA10 USART1 RX Ѫ��

IIC�ӿڣ�
SDA PA4
SCL PA5

����ʹ�ù滮��
����1��Ѫ��
����2������
����3�������ű�Һ����ռ�ã���ӳ�仹�Ǳ�ռ��
����4�������ű�Һ����ռ�ã���ӳ�仹�Ǳ�ռ��
����5���ܷ��������գ���ֱ����


����λ�ã�
USART1 RX PA10
USART1 TX PA9

USART2 RX PA3
USART2 TX PA2

USART3 RX PB11
USART3 TX PB10

UART4 RX P11
UART4 TX P10

UART5 RX PD2
UART5 TX PC12


Һ����ռ�õ����ţ�
ȫ��PB
PC6��PC10

������ռ�õ����ţ�
PC0��PC3��PC13

���ذ���ռ�õ����ţ�
WK_UP PA0
KEY0 PC5
KEY1 PA15

����LEDռ�õ����ţ�
DS0 PA9
DS1 PD2

�����ӿڣ�
PA13 SWDIO
PA14 SWCLK

PB6 ����24C02��SCL
PB7 ����24C02��SDA

�ַ�ȡģ��ʽ��
���룬˳������

˵����
����2�ĺ��������޸���

Ŀǰ���ڵ����⣺
GUI��������ͷ�۵�һ������
Ѫ����עָ���Ľ��������⣬����һ����Ҳ��֪����������Ǹ���ģ��Ȳ�Ҫ�ˡ�
��������ȫ����ɣ����ǽ��治���Ѻã����ִ�����û������ʱӦ�����û�һ����ʾ��
���Ѫ������ͬ��ʧ�ܣ�����Ӧ������һ�£���λ��ҲҪ����һ�������ж����ݸ�ʽ�Ƿ���ȷ��

***************************************************************/
//�Ƿ�ͨ�����ڷ����������ݣ��������ʱֻ���Լ���Ҫ������
//0�����ͣ�1����
//��ʽ����ʱһ��Ҫ��Ϊ1
#define SendAllData 1

#include "sys.h"
#include "usart.h"		
#include "delay.h"	
#include "led.h"   
#include "lcd.h"
#include "math.h"
#include "adc.h"
#include "SendPack.h"
#include "usart2dust.h"
#include "STMIIC.h"
#include "MLX90614.h"
#include "chinese.h"//�����ֿ�
#include "key.h"
#include "read24c16.h"

//------------------------------��������------------------------------//

void DrawECG(void);//�����ĵ�ͼ
void GetSpO2(unsigned char *p);//��ȡѪ��
void DisplayInit(void);//���Ʋ�������֣������ĵ�ͼ�ı��

//��ʾ�����ֿ��еĺ���
//һ����ʾһ������
//�ַ�ȡģ��ʽ�����룬˳������
//������x��λ�ã�y��λ�ã��ֿ����ַ����к�(���)
void LCD_ShowChinese(unsigned short int x, unsigned short int y, unsigned short int word);
void LCD_ShowChinese24(unsigned short int x, unsigned short int y, unsigned short int word);

//------------------------------��������------------------------------//

unsigned short int count = 0;//һ��������

//���²�������Ҫ�����Ĳ���
unsigned short int ECG_axis = 0, ECG_value_old = 0, ECG_value_new = 0;//�ĵ�ͼ�ĺ����꣬�ɡ����ĵ�ֵ
unsigned char TempInt = 0, TempFlo = 0;//�¶ȵ�������С������
unsigned char SpO2 = 0, Pulse = 0;//Ѫ��������
unsigned short int PI = 0;//Perfusion IndexѪ����עָ������дPI
float temperature = 0.0, dust =0.0;//�¶ȣ��۳�Ũ��
unsigned int intdust = 0;//int�͵ķ۳�Ũ��

//���¶��������������ݵ�����
unsigned char ECG_data[2] = {0};//�ĵ�
unsigned char Temp_data[2] = {0};//�¶�
unsigned char dust_data[4] = {0};//�۳�
unsigned char SpO2_data[4] = {0};//Ѫ������������עָ��
unsigned char BG_data[1320] = {0};//Ѫ�ǣ�Blood Glucose��ѪҺ�е�������
unsigned char BG_data_source[2048] = {0};//Ѫ�ǣ�Blood Glucose��ѪҺ�е�������

//------------------------------Let's GO!------------------------------//

int main(void)
{
  	Stm32_Clock_Init(9);//ϵͳʱ������
	uart_init(72, 115200);//����1��ʼ��Ϊ115200������Ѫ���ǵ�ͨ�����ʣ����������λ�ò�Ҫ�ģ�����������
	delay_init(72);
	LED_Init();
 	LCD_Init();
	USART2_Init(36, 2400);//����2��ʼ��Ϊ2400�����Ƿ۳�Ũ�ȼ���ǵ�ͨ������
	Adc_Init();
	IICinit();
	Key_Init();
	DisplayInit();
	
  	while(1)
	{
		delay_ms(6);
		count++;
		
		//�ĵ����ݲɼ�
		ECG_value_new = Get_Adc(ADC_CH1);//���ĵ�
		#if SendAllData
			//�ȷ����ĵ����ݺ��ͼ
			ECG_data[0] = ECG_value_new >> 8;
			ECG_data[1] = ECG_value_new;
			Com1SendArray(ECG_data , 0xE2, 2, 1);
		#endif
		DrawECG();//���ĵ�ͼ
		
		//�۳����ݲɼ�
		if(count == 50)
		{
			//�ֲ���ķ۳�Ũ�ȼ��㹫ʽ
			dust = (float)((Com2RecArr[1] << 8) + Com2RecArr[2]) * 2.686;

			Com2RecComplete = 0;//���ڽ��պ����ֶ�����
			
			#if SendAllData
				//���ͷ۳����ݣ��۳���������100�����൱�ڱ�����λС��
				dust_data[0] = ((int)((dust) * 100)) >> 24;
				dust_data[1] = ((int)((dust) * 100)) >> 16;
				dust_data[2] = ((int)((dust) * 100)) >> 8;
				dust_data[3] = ((int)((dust) * 100));
				Com1SendArray(dust_data, 0xE5, 4, 1);
			#endif
			
			//��ʾ�۳�����
			intdust = (int)dust * 100;
			intdust /= 100;
			LCD_ShowNum(240, 40, intdust, 3, 16);	
		}
		
		//�¶����ݲɼ�
		if(count == 100)
		{
			count = 0;
			
			temperature = ReadTemperatureMLX90614();
			#if SendAllData
				//�����������ݣ�������������100�����൱�ڱ�����λС��
				Temp_data[0] = ((int)((temperature) * 100)) >> 8;
				Temp_data[1] = ((int)((temperature) * 100));
				Com1SendArray(Temp_data, 0xE4, 2, 1);
			#endif
			
			//��ʾ�¶�
			TempInt = (int)temperature;
			TempFlo = (int) (temperature * 10) % 10;
			LCD_ShowNum(60, 40, TempInt, 3, 16);
			LCD_ShowNum(90, 40, TempFlo, 1, 16);
		}
		
		//Ѫ�����ݲɼ�
		if(USART_RX_STA & 0x8000)//�������1�յ�����
		{
			GetSpO2(USART_RX_BUF);//����Ѫ������
			
			#if SendAllData
				//����Ѫ����������Ѫ����עָ������Ѫ����עָ��û������������0��
				SpO2_data[0] = SpO2;
				SpO2_data[1] = Pulse;
				SpO2_data[2] = 0;
				SpO2_data[3] = 0;
				Com1SendArray(SpO2_data, 0xE1, 4, 1);
			#endif
			
			//��ʾѪ��������
			LCD_ShowNum(60, 60, SpO2, 2, 16);
			LCD_ShowNum(200, 60, Pulse, 3, 16);
		}
		
		//�ֶ���ȡѪ������
		if(Key_Scan(0) == 1)
		{
			//��ȡѪ��
			ReadBG(BG_data_source);
			
			LCD_ShowChinese(160, 80, 23);//��ʾ��������
			LCD_ShowChinese(176, 80, 24);//��ʾ��ȡ����
			LCD_ShowChinese(224, 80, 29);//��ʾ��������
			
			//����һ���Ƚ��б�־�Ե��ֽڣ���������ж��Ƿ���������
			if(BG_data_source[2004] == 0x00 || BG_data_source[2004] == 0xFF)
			{
				//��ʾ����ȡʧ�ܣ���
				LCD_ShowChinese(192, 80, 27);//��ʾ��ʧ����
				LCD_ShowChinese(208, 80, 28);//��ʾ���ܡ���
				
			}
			else
			{
				//��ʾ����ȡ�ɹ���
				LCD_ShowChinese(192, 80, 25);//��ʾ���ɡ���
				LCD_ShowChinese(208, 80, 26);//��ʾ��������
				
				DealBG(BG_data_source, BG_data);
				Com1SendArray(BG_data, 0xE3, 1320, 1);
			}
			//����
			delay_ms(1000);
			LCD_Fill(160, 80, 240, 96, WHITE);
		}
	}
}


//--------------------------------����void main(void)�ָ���--------------------------------//


//Ѫ���Ƿ��͵����ݸ�ʽ
//U2:Ѫ��,Ѫ��,Ѫ����עָ��\r\n
void GetSpO2(unsigned char *p)
{
	unsigned char i=0, j=1;//j���ڼ�¼�Ѿ��ҵ��Ķ��ŵĸ���
	unsigned char e;//�˷������ָ��
	unsigned char comma[5] = {0};//���ڱ��涺�ŵ�λ�ã�Ϊ�˷��㴦���0����

	//��һ��������ð�ţ��Ͱ������ɶ��ţ����������ַ����е�λ�ù̶���2��ֱ�Ӷ���
	comma[1] = 2;
	j = 2;//�ӵڶ������ſ�ʼ��
	
	//�ҳ����ж��ŵ�λ��
	for(i=0; i<((USART_RX_STA & 0x3FFF)); i++)
	{
		if(p[i] == 44)//44�Ƕ��ŵ�ASCII��
		{
			comma[j] = i;
			j++;
		}
		
//		if(p[i] == 0x0D)//��0x0DҲ���ɶ��ţ����ڴ���Ѫ����עָ��
//		{
//			comma[j] = i;
//			j++;
//		}
	}
	
	//��1��2����֮�䱣�����Ѫ��
	SpO2 = 0;//������
	e = comma[2] - comma[1] - 2;//��ֵ����λ����Ϊʲô�Ǽ�2��һλ�Ƕ��ţ�10��0�η�����1�����Ի�Ҫ�ټ�һ
	for(i=comma[1]+1; i<comma[2]; i++)//comma[1]+1�������ֿ�ʼ�ĵط�
	{
		//�Ӹ�λ��ʼ��ÿһλ���ۼ�����
		//ascii����48~57������
		SpO2 = (int)(SpO2 + (p[i] - 48) * pow(10, e));
		e--;	
	}
	
	//��2��3����֮�䱣���������
	Pulse = 0;//������
	e = comma[3] - comma[2] - 2;//��ֵ����λ����Ϊʲô�Ǽ�2��һλ�Ƕ��ţ�10��0�η�����1�����Ի�Ҫ�ټ�һ
	for(i=comma[2]+1; i<comma[3]; i++)//comma[1]+1�������ֿ�ʼ�ĵط�
	{
		//�Ӹ�λ��ʼ��ÿһλ���ۼ�����
		//ascii����48~57������
		Pulse = (int)(Pulse + (p[i] - 48) * pow(10, e));
		e--;	
	}
	
	
//��3��4����֮�䱣�����Ѫ����עָ��
//������������⣬�Ȳ�Ҫ��
//	PI = 0;//������
//	e = comma[4] - comma[3] - 2;//��ֵ����λ����Ϊʲô�Ǽ�2��һλ�Ƕ��ţ�10��0�η�����1�����Ի�Ҫ�ټ�һ
//	for(i=comma[3]+1; i<comma[4]; i++)//comma[1]+1�������ֿ�ʼ�ĵط�
//	{
//		//�Ӹ�λ��ʼ��ÿһλ���ۼ�����
//		//ascii����48~57������
//		PI = (int)(PI + (p[i] - 48) * pow(10, e));
//		e--;
//	}
	
	//��������Ժ�һ��Ҫ�ֶ����㣡��
	USART_RX_STA = 0;
}

void LCD_ShowChinese(unsigned short int x, unsigned short int y, unsigned short int word)//һ����ʾһ������
{
	unsigned char tmp;
	unsigned short i, j, k;//ij����ѭ����k���ڿ��ƺ���ͼ������
	
	k = 0;
	for(i=0; i<16; i++)
	{
		tmp = CHS[word][k];
		for(j=0; j<8; j++)
		{
			if(tmp & 0x80)
			{
				LCD_Fast_DrawPoint(x+j, y+i, RED);
			}
			tmp <<= 1;
		}
		k++;
		
		tmp = CHS[word][k];
		for(j=8; j<16; j++)
		{
			if(tmp & 0x80)
			{
				LCD_Fast_DrawPoint(x+j, y+i, RED);
			}
			tmp <<= 1;
		}
		k++;
	}
}

void LCD_ShowChinese24(unsigned short int x, unsigned short int y, unsigned short int word)//һ����ʾһ������
{
	unsigned char tmp;
	unsigned short i, j, k;//ij����ѭ����k���ڿ��ƺ���ͼ������
	
	k = 0;
	for(i=0; i<24; i++)
	{
		tmp = CHS24[word][k];
		for(j=0; j<8; j++)
		{
			if(tmp & 0x80)
			{
				LCD_Fast_DrawPoint(x+j, y+i, BLUE);
			}
			tmp <<= 1;
		}
		k++;
		
		tmp = CHS24[word][k];
		for(j=8; j<16; j++)
		{
			if(tmp & 0x80)
			{
				LCD_Fast_DrawPoint(x+j, y+i, BLUE);
			}
			tmp <<= 1;
		}
		k++;
		
		tmp = CHS24[word][k];
		for(j=16; j<24; j++)
		{
			if(tmp & 0x80)
			{
				LCD_Fast_DrawPoint(x+j, y+i, BLUE);
			}
			tmp <<= 1;
		}
		k++;
	}
}

void DrawECG(void)
{
	unsigned short int i;
	
	ECG_value_new >>= 5;//���ĵ�ֵ��С32�����ĵ�ķ�Χ����129���ڣ����㻭ͼ
	LCD_Fill(ECG_axis, 110, ECG_axis + 3, 240, WHITE);//��Ҫ���ߵ�ǰ����������Ϊ��ɫ���൱�ڲ�����ǰ��ͼ��
	
	for(i=110; i<241; i += 10)
	{
		if(ECG_axis % 10)
		{
			LCD_Fast_DrawPoint(ECG_axis, i, GREEN);//������
		}
		else
		{
			POINT_COLOR=GREEN;
			LCD_DrawLine(ECG_axis, 110, ECG_axis, 240);//������
			POINT_COLOR=RED;
		}
	}

	LCD_DrawLine(ECG_axis, 110 + ECG_value_old, ECG_axis + 1, 110 + ECG_value_new);//�����ĵ�ͼ
	ECG_value_old = ECG_value_new;//��������Ժ�����൱���Ǿ�����
	
	ECG_axis++;
	if(ECG_axis > 320)
	{
		ECG_axis = 0;
	}
}

void DisplayInit(void)
{
	unsigned short int i;
	POINT_COLOR = RED;
	
	//��ʾ���ƽ���Զ�̼໤�ǡ�
	for(i=0; i<8; i++)
	{
		LCD_ShowChinese24(i * 24 + 64, 0, i);
	}
	
	LCD_ShowChinese(20, 40, 0);//��ʾ���¡���
	LCD_ShowChinese(36, 40, 1);//��ʾ���ȡ���
	LCD_ShowChinese(52, 40, 18);//��ʾ��������
	LCD_ShowString(84, 40, 8, 16, 16, ".");//�����¶ȵ�С����
	LCD_ShowChinese(98, 40, 17);//��ʾ���桱��
	
	LCD_ShowChinese(160, 40, 9);//��ʾ���ۡ���
	LCD_ShowChinese(176, 40, 10);//��ʾ��������
	LCD_ShowChinese(192, 40, 11);//��ʾ��Ũ����
	LCD_ShowChinese(208, 40, 12);//��ʾ���ȡ���
	LCD_ShowChinese(224, 40, 18);//��ʾ��������
	LCD_ShowChinese(266, 40, 21);//��ʾ��ug��΢��
	LCD_ShowChinese(282, 40, 22);//��ʾ��/m^3��ÿ������
	
	LCD_ShowChinese(20, 60, 2);//��ʾ��Ѫ����
	LCD_ShowChinese(36, 60, 3);//��ʾ��������
	LCD_ShowChinese(52, 60, 18);//��ʾ��������
	LCD_ShowString(78, 60, 8, 16, 16, "%");//��ʾ��%����
	
	LCD_ShowChinese(160, 60, 4);//��ʾ��������
	LCD_ShowChinese(176, 60, 5);//��ʾ��������
	LCD_ShowChinese(192, 60, 18);//��ʾ��������
	LCD_ShowChinese(226, 60, 19);//��ʾ���Ρ���
	LCD_ShowString(242, 60, 8, 16, 16, "/");//��ʾ��/����
	LCD_ShowChinese(250, 60, 20);//��ʾ���֡���

	LCD_ShowChinese(10, 90, 6);//��ʾ���ġ���
	LCD_ShowChinese(26, 90, 7);//��ʾ���硱��
	LCD_ShowChinese(42, 90, 8);//��ʾ��ͼ����
	LCD_ShowChinese(58, 90, 18);//��ʾ��������
	
	//�����ĵ���
	while(ECG_axis < 321)
	{
		for(i=110; i<241; i += 10)
		{
			if(ECG_axis % 10)
			{
				LCD_Fast_DrawPoint(ECG_axis, i, GREEN);//������
			}
			else
			{
				POINT_COLOR=GREEN;
				LCD_DrawLine(ECG_axis, 110, ECG_axis, 240);//������
				POINT_COLOR=RED;
			}
		}
		ECG_axis++;
	}
}
