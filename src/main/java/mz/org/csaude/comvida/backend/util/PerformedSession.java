package mz.org.fgh.mentoring.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.csaude.comvida.backend.util.SessionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;


@ToString
@NoArgsConstructor
@Setter
@Getter
public class PerformedSession {

    private String district;
    private String healthFacility;
    private String formName;
    private Long totalPerformed;
    private String createdAt;
    private String performedDate;
    private String tutorName;
    private String position;
    private String startDate;
    private String endDate;
    private SessionStatus status;
    private String cabinet;
    private String MENTORING_ID;

    /**
     * Properties of HTS report
     */
    private String tutoredName;
    private String door;
    private String timeOfDay;
    private Long atendidos;
    private Long previos;
    private Long testados;
    private Long positivos;
    private Long inscritos;
    private Long cabinetId;

    /**
     * Properties of Narrative report
     */
    private Long preventionVCT;
    private Long preventionPICT;
    private Long preventionIndexCase;
    private Long preventionSaaj;
    private Long preventionHtcLink;
    private Long preventionANC;
    private Long preventionCPN;
    private Long ctStiAdultsPrison;
    private Long ctAdultsPrison;
    private Long ctAdultsVLPrison;
    private Long ctTbHiv;
    private Long ctApss;
    private Long ctAdults;
    private Long ctAdultsVL;
    private Long ctInh;
    private Long ctTbHivCt;
    private Long ctNutrition;
    private Long ctApssTutoreds;
    private Long ctApssSessions;
    private Long ctEAC;
    private Long ctMDC;
    private Long ctCervical;
    private Long ctStiAdults;
    private Long tbSessions;
    private Long tbSessionsCt;
    private Long tbInh;
    private Long tbSessionsPediatric;
    private Long pediatricNutrition;
    private Long pediatricStarART;
    private Long pediatricAMA;
    private Long pediatricTB;
    private Long pediatricVL;

    /**
     * Properties of POP report
     */
    private String elaborado;
    private String aprovado;
    private String revisado;

    /**
     * Props for PMQ-TR_HIV Report
     */
    private int formacao;
    private int instalacoes;
    private int seguranca;
    private int pretestagem;
    private int testagem;
    private int postestagem;
    private int avaliacao;
    private int total;
    private Long mentorship_id;

    /**
     * COP20 Costed Workplan report
     */
    private Long ind_11061;
    private Long ind_11011;
    private Long ind_11031;
    private Long ind_11041;
    private Long ind_11043;
    private Long ind_11073;
    private Long ind_42   ;
    private Long ind_10043;
    private Long ind_10045;
    private Long ind_04071;
    private Long ind_04073;
    private Long ind_04041;
    private Long ind_04077;
    private Long ind_04078;
    private Long ind_04061;
    private Long ind_15051;
    private Long ind_06044;
    private Long ind_02041;
    private Long ind_01102;
    private Long ind_01031;
    private Long ind_01142;
    private Long ind_02063;
    private Long ind_01116;
    private Long ind_02071;
    private Long ind_02021;
    private Long ind_02023;
    private Long ind_08051;
    private Long ind_03029;
    private Long ind_030211;
    private Long ind_030213;
    private Long ind_03011;
    private Long ind_03013;
    private Long ind_05012;
    private Long ind_05031;
    private Long ind_05061;
    private Long ind_05052;
    private Long ind_05054;
    private Long ind_05057;
    private Long ind_19051;
    private Long ind_19015;

    /**
     * PMQ-TR List
     */

    private String MTQ00000751;
    private String MTQ00000752;
    private String MTQ00000753;
    private String MTQ00000754;
    private String MTQ00000755;
    private String MTQ00000756;
    private String MTQ00000757;
    private String MTQ00000758;
    private String MTQ00000759;
    private String MTQ00000760;
    private String MTQ00000761;
    private String MTQ00000762;
    private String MTQ00000763;
    private String MTQ00000764;
    private String MTQ00000765;
    private String MTQ00000766;
    private String MTQ00000767;
    private String MTQ00000768;
    private String MTQ00000769;
    private String MTQ00000770;
    private String MTQ00000771;
    private String MTQ00000772;
    private String MTQ00000773;
    private String MTQ00000774;
    private String MTQ00000775;
    private String MTQ00000776;
    private String MTQ00000777;
    private String MTQ00000778;
    private String MTQ00000779;
    private String MTQ00000780;
    private String MTQ00000781;
    private String MTQ00000782;
    private String MTQ00000783;
    private String MTQ00000784;
    private String MTQ00000785;
    private String MTQ00000786;
    private String MTQ00000787;
    private String MTQ00000788;
    private String MTQ00000789;
    private String MTQ00000790;
    private String MTQ00000791;
    private String MTQ00000792;
    private String MTQ00000793;
    private String MTQ00000794;
    private String MTQ00000795;
    private String MTQ00000796;
    private String MTQ00000797;
    private String MTQ00000798;
    private String MTQ00000799;
    private String MTQ00000800;
    private String MTQ00000801;
    private String MTQ00000802;
    private String MTQ00000803;
    private String MTQ00000804;
    private String MTQ00000805;
    private String MTQ00000806;
    private String MTQ00000807;
    private String MTQ00000808;
    private String MTQ00000809;
    private String MTQ00000810;
    private String MTQ00000811;
    private String MTQ00000812;
    private String MTQ00000813;
    private String MTQ00000814;










    public PerformedSession(String district, Long ind_11061, Long ind_11011, Long ind_11031, Long ind_11041,
                            Long ind_11043, Long ind_11073, Long ind_42, Long ind_10043, Long ind_10045, Long ind_04071, Long ind_04073,
                            Long ind_04041, Long ind_04077, Long ind_04078, Long ind_04061, Long ind_15051, Long ind_06044, Long ind_02041,
                            Long ind_01102, Long ind_01031, Long ind_01142, Long ind_02063, Long ind_01116, Long ind_02071,
                            Long ind_02021, Long ind_02023, Long ind_08051, Long ind_03029, Long ind_030211, Long ind_030213,
                            Long ind_03011, Long ind_03013, Long ind_05012, Long ind_05031, Long ind_05061, Long ind_05052,
                            Long ind_05054, Long ind_05057, Long ind_19051, Long ind_19015) {
        this.district = district;
        this.ind_11061 = ind_11061;
        this.ind_11011 = ind_11011;
        this.ind_11031 = ind_11031;
        this.ind_11041 = ind_11041;
        this.ind_11043 = ind_11043;
        this.ind_11073 = ind_11073;
        this.ind_42 = ind_42;
        this.ind_10043 = ind_10043;
        this.ind_10045 = ind_10045;
        this.ind_04071 = ind_04071;
        this.ind_04073 = ind_04073;
        this.ind_04041 = ind_04041;
        this.ind_04077 = ind_04077;
        this.ind_04078 = ind_04078;
        this.ind_04061 = ind_04061;
        this.ind_15051 = ind_15051;
        this.ind_06044 = ind_06044;
        this.ind_02041 = ind_02041;
        this.ind_01102 = ind_01102;
        this.ind_01031 = ind_01031;
        this.ind_01142 = ind_01142;
        this.ind_02063 = ind_02063;
        this.ind_01116 = ind_01116;
        this.ind_02071 = ind_02071;
        this.ind_02021 = ind_02021;
        this.ind_02023 = ind_02023;
        this.ind_08051 = ind_08051;
        this.ind_03029 = ind_03029;
        this.ind_030211 = ind_030211;
        this.ind_030213 = ind_030213;
        this.ind_03011 = ind_03011;
        this.ind_03013 = ind_03013;
        this.ind_05012 = ind_05012;
        this.ind_05031 = ind_05031;
        this.ind_05061 = ind_05061;
        this.ind_05052 = ind_05052;
        this.ind_05054 = ind_05054;
        this.ind_05057 = ind_05057;
        this.ind_19051 = ind_19051;
        this.ind_19015 = ind_19015;
    }









    public PerformedSession(String district, String healthFacility, String performedDate,
                            String tutorName, String tutoredName, String cabinet, int formacao, int instalacoes, int seguranca,
                            int pretestagem, int testagem, int postestagem, int avaliacao, int total,
                            String createdAt, Long mentorship_id) {
        this.district = district;
        this.healthFacility = healthFacility;
        this.performedDate = performedDate;
        this.tutorName = tutorName;
        this.tutoredName = tutoredName;
        this.cabinet = cabinet;
        this.formacao = formacao;
        this.setInstalacoes(instalacoes);
        this.setSeguranca(seguranca);
        this.setPretestagem(pretestagem);
        this.setTestagem(testagem);
        this.setPostestagem(postestagem);
        this.setAvaliacao(avaliacao);
        this.setTotal(total);
        this.createdAt = createdAt;
        this.mentorship_id = mentorship_id;
    }



    public PerformedSession(final String formName, final Calendar createdAt, final LocalDate performedDate,
                            final String district, final String healthFacility, final String cabinet, final String tutorName,
                            final String tutorSurname, final String position, final LocalDateTime startDate,
                            final LocalDateTime endDate, final SessionStatus status) {
        this.formName = formName;
        this.createdAt = DateUtils.formatToDDMMYYYY(createdAt.getTime());
        this.performedDate = DateUtils.formatToDDMMYYYY(String.valueOf(performedDate));
        this.district = district;
        this.healthFacility = healthFacility;
        this.cabinet = cabinet;
        this.tutorName = tutorName + " " + tutorSurname;
        this.position = position;
        this.startDate = DateUtils.formatToDDMMYYYY(String.valueOf(startDate));
        this.endDate = DateUtils.formatToDDMMYYYY(String.valueOf(endDate));
        this.status = status;
    }

    public PerformedSession(final String formName, final Long totalPerformed) {
        this.formName = formName;
        this.totalPerformed = totalPerformed;
    }

    public PerformedSession(final String district, final String healthFacility, final Long totalPerformed) {
        this.district = district;
        this.healthFacility = healthFacility;
        this.totalPerformed = totalPerformed;
    }

    /**
     * This constructor will build the HTS Summary
     */
    public PerformedSession(
            final String districtName,
            final String healthFacility,
            final String performedDate,
            final String tutorName,
            final String tutoredName,
            final String cabinet,
            final String door,
            final String timeOfDay,
            final Long atendidos,
            final Long previos,
            final Long testados,
            final Long positivos,
            final Long inscritos,
            final String createdAt,
            final String MENTORING_ID) {
        this.district = districtName;
        this.healthFacility = healthFacility;
        this.performedDate =performedDate;
        this.tutorName = tutorName;
        this.cabinet = cabinet;
        this.tutoredName = tutoredName;
        this.door = door;
        this.timeOfDay = timeOfDay;
        this.atendidos = atendidos;
        this.previos = previos;
        this.testados = testados;
        this.positivos = positivos;
        this.inscritos = inscritos;
        this.createdAt=createdAt;
        this.MENTORING_ID=MENTORING_ID;
    }

    /**
     * This constructor will build the HTS Summary Mobile
     */
    public PerformedSession(
            final String districtName,
            final String healthFacility,
            final String performedDate,
            final String tutorName,
            final String tutoredName,
            final String cabinet,
            final String door,
            final String timeOfDay,
            final Long atendidos,
            final Long previos,
            final Long testados,
            final Long positivos,
            final Long inscritos,
            final String createdAt,
            final Long cabinetId) {
        this.district = districtName;
        this.healthFacility = healthFacility;
        this.performedDate =performedDate;
        this.tutorName = tutorName;
        this.cabinet = cabinet;
        this.tutoredName = tutoredName;
        this.door = door;
        this.timeOfDay = timeOfDay;
        this.atendidos = atendidos;
        this.previos = previos;
        this.testados = testados;
        this.positivos = positivos;
        this.inscritos = inscritos;
        this.createdAt=createdAt;
        this.cabinetId=cabinetId;
    }





    public PerformedSession(String district, Long preventionVCT, Long preventionPICT, Long preventionIndexCase,
                            Long preventionSaaj, Long preventionHtcLink, Long preventionANC, Long preventionCPN, Long ctStiAdultsPrison,
                            Long ctAdultsPrison, Long ctAdultsVLPrison, Long ctTbHiv, Long ctApss, Long ctAdults, Long ctAdultsVL,
                            Long ctInh, Long ctTbHivCt, Long ctNutrition, Long ctApssTutoreds, Long ctHCW, Long ctEAC, Long ctMDC, Long ctCervical, Long ctStiAdults, Long tbSessions,
                            Long tbSessionsCt, Long tbInh, Long tbSessionsPediatric, Long pediatricNutrition, Long pediatricStarART,
                            Long pediatricAMA, Long pediatricTB, Long pediatricVL) {
        this.district = district;
        this.preventionVCT = preventionVCT;
        this.preventionPICT = preventionPICT;
        this.preventionIndexCase = preventionIndexCase;
        this.preventionSaaj = preventionSaaj;
        this.preventionHtcLink = preventionHtcLink;
        this.preventionANC = preventionANC;
        this.preventionCPN = preventionCPN;
        this.ctStiAdultsPrison = ctStiAdultsPrison;
        this.ctAdultsPrison = ctAdultsPrison;
        this.ctAdultsVLPrison = ctAdultsVLPrison;
        this.ctTbHiv = ctTbHiv;
        this.ctApss = ctApss;
        this.ctAdults = ctAdults;
        this.ctAdultsVL = ctAdultsVL;
        this.ctInh = ctInh;
        this.ctTbHivCt = ctTbHivCt;
        this.ctNutrition = ctNutrition;
        this.ctApssTutoreds = ctApssTutoreds;
        this.ctApssSessions = ctHCW;
        this.ctEAC = ctEAC;
        this.ctMDC = ctMDC;
        this.ctCervical = ctCervical;
        this.ctStiAdults = ctStiAdults;
        this.tbSessions = tbSessions;
        this.tbSessionsCt = tbSessionsCt;
        this.tbInh = tbInh;
        this.tbSessionsPediatric = tbSessionsPediatric;
        this.pediatricNutrition = pediatricNutrition;
        this.pediatricStarART = pediatricStarART;
        this.pediatricAMA = pediatricAMA;
        this.pediatricTB = pediatricTB;
        this.pediatricVL = pediatricVL;
    }



    public PerformedSession(String district, String healthFacility, String performedDate, String tutorName, String formName,
                            String elaborado, String aprovado, String revisado, String createdAt) {

        this.district = district;
        this.healthFacility = healthFacility;
        this.performedDate = performedDate;
        this.tutorName = tutorName;
        this.formName = formName;
        this.elaborado = elaborado;
        this.aprovado = aprovado;
        this.revisado = revisado;
        this.createdAt = createdAt;
    }

    public PerformedSession(String district, String healthFacility, String performedDate,
                            String tutorName, String tutoredName, String cabinet, String mTQ00000751, String mTQ00000752, String mTQ00000753,
                            String mTQ00000754, String mTQ00000755, String mTQ00000756, String mTQ00000757, String mTQ00000758,
                            String mTQ00000759, String mTQ00000760, String mTQ00000761, String mTQ00000762, String mTQ00000763,
                            String mTQ00000764, String mTQ00000765, String mTQ00000766, String mTQ00000767, String mTQ00000768,
                            String mTQ00000769, String mTQ00000770, String mTQ00000771, String mTQ00000772, String mTQ00000773,
                            String mTQ00000774, String mTQ00000775, String mTQ00000776, String mTQ00000777, String mTQ00000778,
                            String mTQ00000779, String mTQ00000780, String mTQ00000781, String mTQ00000782, String mTQ00000783,
                            String mTQ00000784, String mTQ00000785, String mTQ00000786, String mTQ00000787, String mTQ00000788,
                            String mTQ00000789, String mTQ00000790, String mTQ00000791, String mTQ00000792, String mTQ00000793,
                            String mTQ00000794, String mTQ00000795, String mTQ00000796, String mTQ00000797, String mTQ00000798,
                            String mTQ00000799, String mTQ00000800, String mTQ00000801, String mTQ00000802, String mTQ00000803,
                            String mTQ00000804, String mTQ00000805, String mTQ00000806, String mTQ00000807, String mTQ00000808,
                            String mTQ00000809, String mTQ00000810, String mTQ00000811, String mTQ00000812, String mTQ00000813,
                            String mTQ00000814, String createdAt, String MENTORING_ID) {
        this.district = district;
        this.healthFacility = healthFacility;
        this.performedDate = performedDate;
        this.tutorName = tutorName;
        this.tutoredName = tutoredName;
        this.cabinet=cabinet;
        this.MTQ00000751 = mTQ00000751;
        this.MTQ00000752 = mTQ00000752;
        this.MTQ00000753 = mTQ00000753;
        this.MTQ00000754 = mTQ00000754;
        this.MTQ00000755 = mTQ00000755;
        this.MTQ00000756 = mTQ00000756;
        this.MTQ00000757 = mTQ00000757;
        this.MTQ00000758 = mTQ00000758;
        this.MTQ00000759 = mTQ00000759;
        this.MTQ00000760 = mTQ00000760;
        this.MTQ00000761 = mTQ00000761;
        this.MTQ00000762 = mTQ00000762;
        this.MTQ00000763 = mTQ00000763;
        this.MTQ00000764 = mTQ00000764;
        this.MTQ00000765 = mTQ00000765;
        this.MTQ00000766 = mTQ00000766;
        this.MTQ00000767 = mTQ00000767;
        this.MTQ00000768 = mTQ00000768;
        this.MTQ00000769 = mTQ00000769;
        this.MTQ00000770 = mTQ00000770;
        this.MTQ00000771 = mTQ00000771;
        this.MTQ00000772 = mTQ00000772;
        this.MTQ00000773 = mTQ00000773;
        this.MTQ00000774 = mTQ00000774;
        this.MTQ00000775 = mTQ00000775;
        this.MTQ00000776 = mTQ00000776;
        this.MTQ00000777 = mTQ00000777;
        this.MTQ00000778 = mTQ00000778;
        this.MTQ00000779 = mTQ00000779;
        this.MTQ00000780 = mTQ00000780;
        this.MTQ00000781 = mTQ00000781;
        this.MTQ00000782 = mTQ00000782;
        this.MTQ00000783 = mTQ00000783;
        this.MTQ00000784 = mTQ00000784;
        this.MTQ00000785 = mTQ00000785;
        this.MTQ00000786 = mTQ00000786;
        this.MTQ00000787 = mTQ00000787;
        this.MTQ00000788 = mTQ00000788;
        this.MTQ00000789 = mTQ00000789;
        this.MTQ00000790 = mTQ00000790;
        this.MTQ00000791 = mTQ00000791;
        this.MTQ00000792 = mTQ00000792;
        this.MTQ00000793 = mTQ00000793;
        this.MTQ00000794 = mTQ00000794;
        this.MTQ00000795 = mTQ00000795;
        this.MTQ00000796 = mTQ00000796;
        this.MTQ00000797 = mTQ00000797;
        this.MTQ00000798 = mTQ00000798;
        this.MTQ00000799 = mTQ00000799;
        this.MTQ00000800 = mTQ00000800;
        this.MTQ00000801 = mTQ00000801;
        this.MTQ00000802 = mTQ00000802;
        this.MTQ00000803 = mTQ00000803;
        this.MTQ00000804 = mTQ00000804;
        this.MTQ00000805 = mTQ00000805;
        this.MTQ00000806 = mTQ00000806;
        this.MTQ00000807 = mTQ00000807;
        this.MTQ00000808 = mTQ00000808;
        this.MTQ00000809 = mTQ00000809;
        this.MTQ00000810 = mTQ00000810;
        this.MTQ00000811 = mTQ00000811;
        this.MTQ00000812 = mTQ00000812;
        this.MTQ00000813 = mTQ00000813;
        this.MTQ00000814 = mTQ00000814;
        this.createdAt = createdAt;
        this.MENTORING_ID = MENTORING_ID;
    }
}
