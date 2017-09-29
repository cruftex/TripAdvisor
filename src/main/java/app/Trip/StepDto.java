package app.Trip;

public class StepDto {
    public String duration ;
    public String html_instructions;
    public Location end_location;

    public static StepDto fromStep(Step step){
        StepDto stepDto = new StepDto();
        stepDto.end_location = step.end_location;
        stepDto.duration = step.duration.text;
        stepDto.html_instructions = step.html_instructions;

        return stepDto;
    }
}
