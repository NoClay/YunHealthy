#include "key.h"
#include "delay.h"


void Key_Init(void)
{
	RCC->APB2ENR |= 1 << 2;
	RCC->APB2ENR |= 1 << 4;
	
	JTAG_Set(SWD_ENABLE);//���������������Ҫ�������ã�����Լ������ӵĻ��Ҳ�֪��
	
	//PA0���ߵ�ƽ��Ч����������
	GPIOA->CRL &= 0xFFFFFFF0;
	GPIOA->CRL |= 0x00000008;
	GPIOA->ODR &= 0xFFFFFFFE;
	
	//PA15�����������ǵ͵�ƽ��Ч����������
	GPIOA->CRH &= 0x0FFFFFFF;
	GPIOA->CRH |= 0x80000000;
	GPIOA->ODR |= 1 << 15;
	
	//PC5
	GPIOC->CRL &= 0xFF0FFFFF;
	GPIOC->CRL |= 0x00800000;
	GPIOC->ODR |= 1 << 5;
}

//����������
//���ذ���ֵ
//mode:0,��֧��������;1,֧��������;
//
//����ֵ��
//0��û���κΰ�������
//KEY0_PRES��KEY0����
//KEY1_PRES��KEY1����
//WKUP_PRES��WK_UP���� 
//ע��˺�������Ӧ���ȼ�,KEY0>KEY1>WK_UP!!
u8 Key_Scan(u8 mode)
{	 
	static u8 key_up = 1;//�������ɿ���־��1Ϊ�ɿ���0Ϊ���£�ͬʱΪʵ���������̵�
	if(mode)
	{
		key_up=1;//֧������
	}
	if(key_up && (KEY0==0 || KEY1==0 || WK_UP==1))
	{
		delay_ms(10);//ȥ��
		key_up=0;
		if(KEY0==0)
		{
			return KEY0_PRES;//�൱��return 1;
		}
		else if(KEY1==0)
		{
			return KEY1_PRES;//�൱��return 2;
		}
		else if(WK_UP==1)
		{
			return WKUP_PRES;//�൱��return 3;
		}
	}
	else if(KEY0==1 && KEY1==1 && WK_UP==0)
	{
		key_up=1;
	}
	return 0;// �ް�������
}
