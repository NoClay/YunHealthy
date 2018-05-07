#include "key.h"
#include "delay.h"

//����״̬��ʶ
//0 �ް���
//1 �̰�
//2 ����
unsigned char KeyStatus = 0;

void Key_Init(void)
{
	RCC->APB2ENR |= 0x00000008;//ʹ��PBʱ��
	
	//PB13��Ϊ����������ʱΪ�͵�ƽ
	GPIOB->CRH &= 0xFF0FFFFF;
	GPIOB->CRH |= 0x00800000;//PB13��������
	GPIOB->ODR |= 0x00002000;//����ߵ�ƽ
	
	//�����жϷ���
	Ex_NVIC_Config(GPIO_B, 13, FTIR);//�½��ش���
	MY_NVIC_Init(2,2,EXTI15_10_IRQn,2);//��ռ2�������ȼ�2����2
}

//��������������ͨ��
//����ֵ��
//0��û�а�
//1���̰���500ms������Ϊ�Ƕ̰�
//2������
//unsigned char Key_Scan(void)
//{
//	static unsigned short int KeyCount = 0;//

//	if(KEY == 0)
//	{
//		delay_ms(10);//ȥ��

//		if(KEY == 0)
//		{
//			KeyCount++;
//		}
//	}
//	
//	if(KEY == 1 && KeyCount > 50)//�����������50�ξ���500ms
//	{
//		KeyCount = 0;
//		return 2;
//	}
//	if(KEY == 1 && KeyCount >= 1)
//	{
//		KeyCount = 0;
//		return 1;
//	}
//	
//	return 0;// �ް�������
//}


//�������������жϰ�
//�ⲿ�ж�10��15���жϷ�����
//������PB13
//�����޸ģ����ż����Ź֣�
void EXTI15_10_IRQHandler(void)
{
	delay_ms(10);//ȥ��

	if(KEY == 0)
	{
		KeyStatus = 1;
	}
	
	//0.4s����������������£�����Ϊ�ǳ���
	//������������һ��bug���������һ�£��ɿ��ˣ�Ȼ�������ֱ��0.4�룬Ҳ�ᱻ����Ϊ����
	delay_ms(400);
	
	if(KEY == 0)
	{
		KeyStatus = 2;
	}
	
	EXTI->PR = 0x000002000;//����жϱ�־λ
}
