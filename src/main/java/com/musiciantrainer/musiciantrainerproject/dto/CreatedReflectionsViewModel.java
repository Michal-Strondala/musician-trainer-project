package com.musiciantrainer.musiciantrainerproject.dto;

import com.musiciantrainer.musiciantrainerproject.entity.Reflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@Getter
@Setter
public class CreatedReflectionsViewModel {

    private List<Reflection> createdReflections;
}
